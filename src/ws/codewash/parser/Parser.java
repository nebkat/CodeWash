package ws.codewash.parser;

import com.florianingerl.util.regex.Matcher;
import ws.codewash.java.CWClass;
import ws.codewash.java.CWClassOrInterface;
import ws.codewash.java.CWEnum;
import ws.codewash.java.CWInterface;
import ws.codewash.parser.ParsedSourceTree.Source;

import java.io.IOException;
import java.lang.reflect.Array;
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

	//private static final Pattern DEFINITION_GENERIC_TYPE_PATTERN = Pattern.compile("(?'cw_identifier')(?:\\s+extends\\s+(?'cw_type'))?");
	//private static final Pattern DEFINITION_GENERIC_MULTIPLE_TYPE_PATTERN = Pattern.compile("(?'cw_canonical')(?:\\s*,\\s*(?'cw_canonical'))*")

	private static final Pattern MULTIPLE_CANONICAL_PATTERN = Pattern.compile("(?<cw_multiple_canonical>(?'cw_canonical')(?:\\s*,\\s*(?'cw_canonical'))*)").register();
	private static final Pattern TYPE_PATTERN = Pattern.compile("(?<cw_type>(?<cw_type_name>(?'cw_canonical'))(?<cw_type_generic>\\s*<\\s*(?'cw_type')(?:\\s*,\\s*(?'cw_type'))*\\s*>\\s*)?(?<cw_type_arrays>(?:\\s*\\[\\s*\\]\\s*)+)?)").register();
	private static final Pattern MULTIPLE_TYPE_PATTERN = Pattern.compile("(?<cw_multiple_type>(?'cw_type')(?:\\s*,\\s*(?'cw_type'))*)").register();

	private static final Pattern PARENTHESES_PATTERN = Pattern.compile("(?<cw_parentheses>\\{(?:[^{}]+|(?'cw_parentheses'))*\\})").register();
	private static final Pattern BLOCK_PATTERN = Pattern.compile("(?<cw_block>\\{(?:[^{}]+|(?'cw_block'))*\\})").register();
	private static final Pattern GENERIC_PATTERN = Pattern.compile("(?<cw_generic><(?:[^<>]+|(?'cw_generic'))*>)").register();

	private static final Pattern ANNOTATION_PATTERN = Pattern.compile("(?<cw_annotation>@\\s*(?<annotation_name>(?'cw_canonical'))(\\s+(?'cw_parentheses')\\s*)?)").register();
	private static final Pattern MODIFIER_PATTERN = Pattern.compile("(?<cw_modifier>(public|protected|private|abstract|static|final|(?'cw_annotation')))").register();
	private static final Pattern MULTIPLE_MODIFIERS_PATTERN = Pattern.compile("(?<cw_multiple_modifier>(?'cw_modifier')(?:\\s+(?'cw_modifier'))*)").register();

	private static final Pattern PACKAGE_PATTERN = Pattern.compile("\\s*package\\s+(?<package>(?'cw_canonical'))\\s*;");
	private static final Pattern IMPORT_PATTERN = Pattern.compile(
			"\\s*import\\s+(?<canonical>(?<package>(?'cw_identifier')" +
					"(?:\\s*\\.\\s*(?'cw_identifier'))*)" +
					"\\s*\\.\\s*(?<class>(?'cw_identifier')|\\*))\\s*;");

	// TODO: Generics
	private static final Pattern CLASS_DECLARATION_PATTERN = Pattern.compile(
			"\\s*(?:(?<modifiers>(?'cw_multiple_modifier'))\\s+)?" +
					"class\\s+(?<name>(?'cw_identifier'))" +
					"(\\s+(?<generic>(?'cw_generic')))?" +
					"(\\s+extends\\s+(?<super>(?'cw_type')))?" +
					"(\\s+implements\\s+(?<interfaces>(?'cw_multiple_type')))?" +
					"\\s*(?<block>(?'cw_block'))");


	private static final Pattern INTERFACE_DECLARATION_PATTERN = Pattern.compile(
			"\\s*(?:(?<modifiers>(?'cw_multiple_modifier'))\\s+)?" +
					"interface\\s+(?<name>(?'cw_identifier'))" +
					"(\\s+(?<generic>(?'cw_generic')))?" +
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

	private ParsedSourceTree mSourceTree;

	public ParsedSourceTree parse(List<Path> rawSources) throws IOException {
		ParsedSourceTree sourceTree = new ParsedSourceTree(rawSources);

		System.out.println("Parsing source tree");

		// Preprocess
		// TODO: Generic preprocessor
		CommentPreProcessor c = new CommentPreProcessor();
		for (Source s : sourceTree.getSources()) {
			System.out.print("\rRemoving comments in " + s.getName());
			s.setProcessedContent(c.process(s.getProcessedContent()));
		}
		System.out.println();

		mSourceTree = sourceTree;

		sourceTree.getSources().parallelStream().forEach(this::parsePackage);
		System.out.println();
		sourceTree.getSources().parallelStream().forEach(this::detectClassDeclarations);
		System.out.println();
		sourceTree.getSources().parallelStream().forEach(this::parseImports);
		System.out.println();
		sourceTree.getSources().parallelStream().forEach(this::initializeClasses);
		System.out.println();
		System.out.println("Resolving super classes, outer classes, implemented interfaces, arrays and generic types");
		sourceTree.resolvePendingTypes();

		System.out.println("Classes found: " + sourceTree.getClasses().values().size() + "\n");
		for (CWClassOrInterface classOrInterface : sourceTree.getClasses().values()) {
			System.out.println(classOrInterface);
		}

		return sourceTree;
	}

	private void parsePackage(Source source) {
		System.out.print("\r" + "Parsing package in " + source.getName());

		String content = source.getProcessedContent();

		Matcher packageMatcher = PACKAGE_PATTERN.matcher(content);
		while (packageMatcher.find()) {
			if (packageMatcher.start() == 0) {
				source.setPackage(packageMatcher.group("package"));
			} else {
				// TODO: Handle
				throw new IllegalStateException("Error parsing " + source.getName() + " | Package statement not at top.");
			}
		}
	}

	private void parseImports(Source source) {
		System.out.print("\r" + "Parsing imports in " + source.getName());

		String content = source.getProcessedContent();

		Matcher importMatcher = IMPORT_PATTERN.matcher(content);
		while (importMatcher.find()) {
			String importCanonical = importMatcher.group("canonical");
			String importPackage = importMatcher.group("package");
			String importClass = importMatcher.group("class");

			if (importMatcher.start() > source.getDeclarationStartLocation()) {
				// TODO: Handle
				throw new IllegalStateException("import statement found after class declarations");
			}

			// Single or on demand (package.*)
			if (importClass.equals("*")) {
				source.addOnDemandTypeImport(importPackage);
			} else {
				source.addSingleTypeImport(importClass, importCanonical);
			}
		}
	}

	private void detectClassDeclarations(Source source) {
		System.out.print("\r" + "Detecting classes in " + source.getName());
		processClasses(source, false, new Stack<>(), source.getProcessedContent(), 0);
	}

	private void initializeClasses(Source source) {
		System.out.print("\r" + "Initializing classes in " + source.getName());
		processClasses(source, true, new Stack<>(), source.getProcessedContent(), 0);
	}

	private void processClasses(Source source, boolean initOrDetect, Stack<String> enclosingClassStack, String content, int offset) {
		Matcher classMatcher = CLASS_DECLARATION_PATTERN.matcher(content);
		Matcher interfaceMatcher = INTERFACE_DECLARATION_PATTERN.matcher(content);
		Matcher enumMatcher = ENUM_DECLARATION_PATTERN.matcher(content);
		Matcher annotationMatcher = ANNOTATION_DECLARATION_PATTERN.matcher(content);

		List<Matcher> matchers = new ArrayList<>(Arrays.asList(classMatcher, interfaceMatcher, enumMatcher, annotationMatcher));

		int index = 0;
		while (true) {
			final int searchIndex = index;
			matchers.removeIf(m -> (searchIndex == 0 || m.start() < searchIndex) && !m.find(searchIndex));
			if (matchers.size() == 0) return;
			Matcher matcher = matchers.stream().min(Comparator.comparing(Matcher::start)).get();

			String name = matcher.group("name");
			String localName = dot(String.join(".", enclosingClassStack)) + name;
			if (initOrDetect) {
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
							// TODO: Handle
							throw new IllegalStateException("Type is not valid");
						}

						String type = typeMatcher.group("cw_type_name");
						String resolvedType = source.resolveType(type, enclosingClassStack);

						if (resolvedType == null) {
							// TODO: Handle
							throw new IllegalStateException("Could not find interface " + type);
						}

						interfaces.add(resolvedType);
					}
				}

				CWClassOrInterface classOrInterface = null;
				if (matcher == classMatcher) {
					String superClass = Object.class.getName();
					if (matcher.group("super") != null) {
						Matcher typeMatcher = TYPE_PATTERN.matcher(matcher.group("super").trim());
						if (!typeMatcher.matches()) {
							// TODO: Handle
							throw new IllegalStateException("Type is not valid");
						}

						String type = typeMatcher.group("cw_type_name");
						String resolvedType = source.resolveType(type, enclosingClassStack);

						if (resolvedType == null) {
							// TODO: Handle
							throw new IllegalStateException("Could not find superclass " + type);
						}

						superClass = resolvedType;
					}

					classOrInterface = new CWClass(source.getPackage(), modifiers, name, superClass, enclosingClassStack, interfaces);
				} else if (matcher == interfaceMatcher) {
					classOrInterface = new CWInterface(source.getPackage(), modifiers, name, enclosingClassStack, interfaces);
				} else if (matcher == enumMatcher) {
					classOrInterface = new CWEnum(source.getPackage(), modifiers, name, enclosingClassStack, interfaces);
				} else if (matcher == annotationMatcher) {
					throw new IllegalStateException("Cannot parse annotations");
				}

				mSourceTree.addClass(classOrInterface);
			} else {
				source.addClassDeclaration(localName);
				source.setDeclarationStartLocation(offset + matcher.start());
			}

			enclosingClassStack.push(name);
			processClasses(source, initOrDetect, enclosingClassStack, matcher.group("block"), offset + matcher.start("block"));
			enclosingClassStack.pop();

			index = matcher.end();
		}
	}
}

