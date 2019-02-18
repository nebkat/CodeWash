package ws.codewash.parser;

import com.florianingerl.util.regex.Matcher;
import ws.codewash.java.CWArray;
import ws.codewash.java.CWClass;
import ws.codewash.java.CWClassOrInterface;
import ws.codewash.java.CWEnum;
import ws.codewash.java.CWInterface;
import ws.codewash.parser.ParsedSourceTree.Source;
import ws.codewash.parser.exception.IllegalFormatParseException;
import ws.codewash.parser.exception.UnexpectedTokenParseException;
import ws.codewash.parser.exception.UnresolvedTypeParseException;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Stream;

import static ws.codewash.parser.ParsedSourceTree.dot;

public class Parser {
	private static final Map<String, Integer> MODIFIERS = new HashMap<>() {{
		put("public", Modifier.PUBLIC);
		put("protected", Modifier.PROTECTED);
		put("private", Modifier.PRIVATE);

		put("abstract", Modifier.ABSTRACT);
		put("static", Modifier.STATIC);
		put("final", Modifier.FINAL);
	}};

	public static final String[] DEFAULT_IMPORTS = {"java.lang", "java.lang.annotation", "java.lang.instrument",
			"java.lang.invoke", "java.lang.management", "java.lang.ref", "java.lang.reflect"};

	private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("(?<cw_identifier>\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*)").register();
	private static final Pattern CANONICAL_PATTERN = Pattern.compile("(?<cw_canonical>(?'cw_identifier')(?:\\s*\\.\\s*(?'cw_identifier'))*)").register();
	private static final Pattern MULTIPLE_CANONICAL_PATTERN = Pattern.compile("(?<cw_multiple_canonical>(?'cw_canonical')(?:\\s*,\\s*(?'cw_canonical'))*)").register();
	private static final Pattern TYPE_PATTERN = Pattern.compile("(?<cw_type>(?<cw_type_name>(?'cw_canonical'))(?<cw_type_generic>\\s*<\\s*(?'cw_type')(?:\\s*,\\s*(?'cw_type'))*\\s*>\\s*)?(?<cw_type_arrays>(?:\\s*\\[\\s*\\]\\s*)+)?)").register();
	private static final Pattern MULTIPLE_TYPE_PATTERN = Pattern.compile("(?<cw_multiple_type>(?'cw_type')(?:\\s*,\\s*(?'cw_type'))*)").register();

	private static final Pattern PARENTHESES_PATTERN = Pattern.compile("(?<cw_parentheses>\\{(?:[^{}]+|(?'cw_parentheses'))*\\})").register();
	private static final Pattern BLOCK_PATTERN = Pattern.compile("(?<cw_block>\\{(?:[^{}]+|(?'cw_block'))*\\})").register();
	private static final Pattern GENERIC_PATTERN = Pattern.compile("(?<cw_generic><(?:[^<>]+|(?'cw_generic'))*>)").register();

	private static final Pattern ANNOTATION_PATTERN = Pattern.compile("(?<cw_annotation>@\\s*(?<annotation_name>(?'cw_canonical'))(\\s+(?'cw_parentheses')\\s*)?)").register();
	private static final Pattern MODIFIER_PATTERN = Pattern.compile("(?<cw_modifier>(public|protected|private|abstract|static|final|strictfp|transient|volatile|(?'cw_annotation')))").register();
	private static final Pattern MULTIPLE_MODIFIERS_PATTERN = Pattern.compile("(?<cw_multiple_modifier>(?'cw_modifier')(?:\\s+(?'cw_modifier'))*)").register();

	private static final Pattern PACKAGE_PATTERN = Pattern.compile("\\s*package\\s+(?<package>(?'cw_canonical'))\\s*;");
	private static final Pattern IMPORT_PATTERN = Pattern.compile(
			"\\s*import\\s+(?<static>static\\s+)?(?<canonical>(?<package>(?'cw_identifier')" +
					"(?:\\s*\\.\\s*(?'cw_identifier'))*)" +
					"\\s*\\.\\s*(?<class>(?'cw_identifier')|\\*))\\s*;");

	private static final Pattern CLASS_DECLARATION_PATTERN = Pattern.compile(
			"\\s*(?:(?<modifiers>(?'cw_multiple_modifier'))\\s+)?" +
					"class\\s+(?<name>(?'cw_identifier'))" +
					"(\\s*(?<generic>(?'cw_generic')))?" +
					"(\\s+extends\\s+(?<super>(?'cw_type')))?" +
					"(\\s+implements\\s+(?<interfaces>(?'cw_multiple_type')))?" +
					"\\s*(?<block>(?'cw_block'))");


	private static final Pattern INTERFACE_DECLARATION_PATTERN = Pattern.compile(
			"\\s*(?:(?<modifiers>(?'cw_multiple_modifier'))\\s+)?" +
					"interface\\s+(?<name>(?'cw_identifier'))" +
					"(\\s*(?<generic>(?'cw_generic')))?" +
					"(\\s+extends\\s+(?<interfaces>(?'cw_multiple_type')))?" +
					"\\s*(?<block>(?'cw_block'))");

	private static final Pattern ENUM_DECLARATION_PATTERN = Pattern.compile(
			"\\s*(?:(?<modifiers>(?'cw_multiple_modifier'))\\s+)?" +
					"enum\\s+(?<name>(?'cw_identifier'))" +
					"(\\s+implements\\s+(?<interfaces>(?'cw_multiple_type')))?" +
					"\\s*(?<block>(?'cw_block'))");

	private static final Pattern ANNOTATION_DECLARATION_PATTERN = Pattern.compile(
			"\\s*(?:(?<modifiers>(?'cw_multiple_modifier'))\\s+)?" +
					"@\\s*interface\\s+(?<name>(?'cw_identifier'))" +
					"\\s*(?<block>(?'cw_block'))");

	private static final Pattern INITIALIZER_PATTERN = Pattern.compile(
			"\\s*(?<static>static)?(?<block>(?'cw_block'))");

	private static final Pattern CONSTRUCTOR_PATTERN = Pattern.compile(
			"\\s*(?:(?<modifiers>(?'cw_multiple_modifier'))\\s+)?" +
					"(?<name>(?'cw_identifier'))" +
					"\\s*(?<parameters>(?'cw_parentheses'))" +
					"\\s*(?<block>(?'cw_block'))");

	private static final Pattern FIELD_PATTERN = Pattern.compile("");
	private static final Pattern METHOD_PATTERN = Pattern.compile("");

	private ParsedSourceTree mSourceTree;

	public ParsedSourceTree parse(List<Path> rawSources) throws IOException {
		ParsedSourceTree sourceTree = new ParsedSourceTree(rawSources);

		System.out.println("Parsing source tree");

		// Preprocess
		// TODO: Generic preprocessor
		CommentPreProcessor c = new CommentPreProcessor();
		for (Source s : sourceTree.getSources()) {
			System.out.println("Removing comments in " + s.getName());
			s.setProcessedContent(c.process(s.getProcessedContent()));
		}

		mSourceTree = sourceTree;

		long startTime = System.currentTimeMillis();

		sourceTree.getSources().parallelStream().forEach(this::parsePackage);
		sourceTree.getSources().parallelStream().forEach(this::parseImports);
		sourceTree.getSources().parallelStream().forEach(this::detectClassDeclarations);
		sourceTree.getSources().parallelStream().forEach(this::initializeClasses);
		System.out.println("Resolving super classes, outer classes, implemented interfaces, arrays and generic types");
		sourceTree.resolvePendingTypes();

		long endTime = System.currentTimeMillis();

		System.out.println();
		System.out.println("-------------------------");
		System.out.println("Parsing complete");
		System.out.println("-------------------------");
		System.out.println(sourceTree.getClasses().size() + " classes parsed/loaded in " + ((endTime - startTime) / 1000.0) + "s");
		System.out.println();

		return sourceTree;
	}

	private void parsePackage(Source source) {
		System.out.println("Parsing package in " + source.getName());

		String content = source.getProcessedContent();

		Matcher packageMatcher = PACKAGE_PATTERN.matcher(content);
		if (packageMatcher.find()) {
			if (packageMatcher.start() != 0) {
				throw new UnexpectedTokenParseException("Unexpected token encountered above package declaration.", source, 0);
			}

			// File package name
			source.setPackage(packageMatcher.group("package"));

			// Imports are directly after package
			source.setImportStartLocation(packageMatcher.end());
		}
	}

	private void parseImports(Source source) {
		System.out.println("Parsing imports in " + source.getName());

		String content = source.getProcessedContent();

		Matcher importMatcher = IMPORT_PATTERN.matcher(content);

		int index = source.getImportStartLocation();
		while (importMatcher.find(index)) {
			if (importMatcher.start() != index) {
				throw new UnexpectedTokenParseException("Unexpected token encountered, import statements expected.", source, index);
			}

			String importCanonical = importMatcher.group("canonical");
			String importPackage = importMatcher.group("package");
			String importClass = importMatcher.group("class");

			if (importMatcher.group("static") == null) {
				// Single or on demand (package.*)
				if (importClass.equals("*")) {
					source.addOnDemandTypeImport(importPackage);
				} else {
					source.addSingleTypeImport(importClass, importCanonical, importMatcher.start());
				}
			} else {
				// Single or on demand (package.*)
				if (importClass.equals("*")) {
					source.addOnDemandStaticImport(importPackage);
				} else {
					source.addSingleStaticImport(importClass, importCanonical);
				}
			}

			index = importMatcher.end();
		}

		// Declarations are directly after imports
		source.setDeclarationStartLocation(index);
	}

	private void detectClassDeclarations(Source source) {
		System.out.println("Detecting classes in " + source.getName());
		processClasses(source, false, new Stack<>(), null, source.getProcessedContent(), 0);
	}

	private void initializeClasses(Source source) {
		System.out.println("Initializing classes in " + source.getName());
		processClasses(source, true, new Stack<>(), null, source.getProcessedContent(), 0);
	}

	private void processClasses(Source source, boolean initOrDetect, Stack<String> enclosingClassStack, CWClassOrInterface enclosingClass, String content, int offset) {
		Matcher classMatcher = CLASS_DECLARATION_PATTERN.matcher(content);
		Matcher interfaceMatcher = INTERFACE_DECLARATION_PATTERN.matcher(content);
		Matcher enumMatcher = ENUM_DECLARATION_PATTERN.matcher(content);
		Matcher annotationMatcher = ANNOTATION_DECLARATION_PATTERN.matcher(content);

		List<Matcher> matchers = new ArrayList<>(Arrays.asList(classMatcher, interfaceMatcher, enumMatcher, annotationMatcher));

		final int initialIndex = enclosingClassStack.empty() ? source.getDeclarationStartLocation() : 0;
		int index = initialIndex;
		while (true) {
			final int searchIndex = index;
			matchers.removeIf(m -> (searchIndex == initialIndex || m.start() < searchIndex) && !m.find(searchIndex));
			if (matchers.size() == 0) return;
			Matcher matcher = matchers.stream().min(Comparator.comparing(Matcher::start)).get();

			if (enclosingClassStack.empty() && matcher.start() != searchIndex) {
				throw new UnexpectedTokenParseException("Unexpected token encountered, class declarations expected.", source, searchIndex);
			}

			CWClassOrInterface classOrInterface = null;

			String name = matcher.group("name");
			String localName = dot(String.join(".", enclosingClassStack)) + name;
			if (!initOrDetect) {
				source.addClassDeclaration(localName);
			} else {
				int modifiers = 0;
				if (matcher.group("modifiers") != null) {
					modifiers = Stream.of(matcher.group("modifiers").split("\\s+"))
							.map(String::trim)
							.filter(MODIFIERS::containsKey)
							.mapToInt(MODIFIERS::get)
							.reduce(0, (a, b) -> a | b);
				}

				Set<String> interfaces = new HashSet<>();
				if (matcher.group("interfaces") != null) {
					for (String interfaceType : matcher.group("interfaces").split(",")) {
						Matcher typeMatcher = TYPE_PATTERN.matcher(interfaceType.trim());
						if (!typeMatcher.matches()) {
							throw new IllegalFormatParseException("Type " + interfaceType + " is not valid", source, matcher.start("interfaces"));
						}

						String type = typeMatcher.group("cw_type_name");
						String resolvedType = source.resolveFullName(type, enclosingClassStack);

						if (resolvedType == null) {
							throw new UnresolvedTypeParseException("Could not resolve interface " + type, source, matcher.start("interfaces"));
						}

						interfaces.add(resolvedType);
					}
				}

				if (matcher == classMatcher) {
					String superClass = Object.class.getName();
					if (matcher.group("super") != null) {
						Matcher typeMatcher = TYPE_PATTERN.matcher(matcher.group("super").trim());
						if (!typeMatcher.matches()) {
							throw new IllegalFormatParseException("Type " + matcher.group("super") + " is not valid", source, matcher.start("super"));
						}

						String type = typeMatcher.group("cw_type_name");
						String resolvedType = source.resolveFullName(type, enclosingClassStack);

						if (resolvedType == null) {
							throw new UnresolvedTypeParseException("Could not resolve superclass " + type, source, matcher.start("super"));
						}

						superClass = resolvedType;
					}

					classOrInterface = new CWClass(mSourceTree, source.getPackage(), modifiers, name, superClass, enclosingClass, interfaces);
				} else if (matcher == interfaceMatcher) {
					classOrInterface = new CWInterface(mSourceTree, source.getPackage(), modifiers, name, enclosingClass, interfaces);
				} else if (matcher == enumMatcher) {
					classOrInterface = new CWEnum(mSourceTree, source.getPackage(), modifiers, name, enclosingClass, interfaces);
				} else if (matcher == annotationMatcher) {
					// TODO:
					throw new IllegalStateException("Cannot parse annotations");
				}

				mSourceTree.addType(classOrInterface);
			}

			enclosingClassStack.push(name);
			processClasses(source, initOrDetect, enclosingClassStack, classOrInterface, matcher.group("block"), offset + matcher.start("block"));
			enclosingClassStack.pop();

			index = matcher.end();
		}
	}
}

