package ws.codewash.parser;

import ws.codewash.java.CWClass;
import ws.codewash.java.CWClassOrInterface;
import ws.codewash.java.CWConstructor;
import ws.codewash.java.CWConstructorOrMethod;
import ws.codewash.java.CWEnum;
import ws.codewash.java.CWField;
import ws.codewash.java.CWInitializer;
import ws.codewash.java.CWInterface;
import ws.codewash.java.CWMethod;
import ws.codewash.java.CWParameterizable;
import ws.codewash.java.CWTypeParameter;
import ws.codewash.java.CWVariable;
import ws.codewash.java.Scope;
import ws.codewash.parser.grammar.Grammar;
import ws.codewash.parser.tree.LexicalTree;
import ws.codewash.parser.tree.LexicalTreeNode;
import ws.codewash.parser.tree.SyntacticTree;
import ws.codewash.parser.tree.SyntacticTreeNode;
import ws.codewash.util.Log;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static ws.codewash.util.Timing.*;

public class Parser {
	public static final boolean DEBUG = false;
	private static final String TAG = "Parser";

	private static final Map<String, Integer> MODIFIERS = new HashMap<>() {{
		put("public", Modifier.PUBLIC);
		put("protected", Modifier.PROTECTED);
		put("private", Modifier.PRIVATE);

		put("default", 0);
		put("abstract", Modifier.ABSTRACT);
		put("static", Modifier.STATIC);
		put("final", Modifier.FINAL);

		put("native", Modifier.NATIVE);
		put("strictfp", Modifier.STRICT);
		put("synchronized", Modifier.SYNCHRONIZED);
		put("transient", Modifier.TRANSIENT);
		put("volatile", Modifier.VOLATILE);
	}};

	private Grammar mGrammar;
	private ParsedSourceTree mParsedSourceTree;

	public Parser(Grammar grammar) {
		mGrammar = grammar;
	}

	public ParsedSourceTree parse(List<Path> paths) throws IOException {
		mParsedSourceTree = new ParsedSourceTree(paths);

		Set<CompilationUnit> sources = mParsedSourceTree.getSources();
		long start;

		// Translate unicode
		start = time();
		sources.parallelStream().forEach(this::translateUnicode);
		Log.d(TAG, "Translated unicode escapes in " + duration(start, time()) + "s");

		// Parse lexical tree
		start = time();
		sources.parallelStream().forEach(this::processLexical);
		Log.d(TAG, "Parsed lexical grammar in " + duration(start, time()) + "s");

		// Parse syntactic tree
		start = time();
		sources.parallelStream().forEach(this::processSyntactic);
		Log.d(TAG, "Parsed syntactic grammar in " + duration(start, time()) + "s");

		// Process declarations
		start = time();
		sources.parallelStream().forEach(this::processDeclarations);
		Log.d(TAG, "Processed declarations in " + duration(start, time()) + "s");

		return mParsedSourceTree;
	}

	private void translateUnicode(CompilationUnit unit) {
		LexicalTreeNode unicodeInput = mGrammar.getProduction("UnicodeInput")
				.match(unit, unit.getContent(), 0, "");

		StringBuilder builder = new StringBuilder();

		List<LexicalTreeNode> unicodeInputCharacters = unicodeInput.get().getAll();
		for (LexicalTreeNode unicodeInputCharacter : unicodeInputCharacters) {
			unicodeInputCharacter = unicodeInputCharacter.get();
			String characterType = unicodeInputCharacter.getSymbolName();
			switch (characterType) {
				case "UnicodeEscape":
					String unicodeEscape = unicodeInputCharacter.getContent();
					if (unicodeEscape.length() > 6) {
						builder.append("\\").append(unicodeEscape.substring(2));
					} else {
						String hex = unicodeEscape.substring(2);
						char unicode = (char) Integer.parseInt(hex, 16);
						builder.append(unicode);
					}
					break;
				case "InvalidUnicodeEscape":
					// TODO: Handle
					throw new IllegalStateException("Invalid unicode escape detected");
				default:
					builder.append(unicodeInputCharacter.getContent());
					break;
			}
		}

		unit.setContent(builder.toString());
	}

	private void processLexical(CompilationUnit unit) {
		// Generate lexical tree
		LexicalTreeNode lexicalInput = mGrammar.getProduction("Input")
				.match(unit, unit.getContent(), 0, "");

		if (lexicalInput.getContentLength() != unit.getContentLength()) {
			// TODO: Handle
			throw new IllegalStateException("Could not parse input at " + unit.getFileName() + ":" + lexicalInput.length());
		}

		unit.setLexicalTree(new LexicalTree(lexicalInput));

		// Extract tokens from lexical tree
		List<Token> tokens = lexicalInput.get("{InputElement}").getAll().stream()
				.map(inputElement -> inputElement.get("Token"))
				.filter(Objects::nonNull)
				.map(LexicalTreeNode::get)
				.map(Token::new)
				.collect(Collectors.toList());
		unit.setTokens(tokens);
	}

	private void processSyntactic(CompilationUnit unit) {
		// Generate syntactic tree
		List<SyntacticTreeNode> matches = mGrammar.getProduction("CompilationUnit")
				.match(unit, unit.getTokens(), 0, "");

		// Complete parsing fail, should never happen
		if (matches.size() == 0) {
			// TODO: Handle
			throw new IllegalStateException("Parsing failed");
		}

		SyntacticTreeNode syntacticCompilationUnit = matches.get(matches.size() - 1);

		if (syntacticCompilationUnit.getContentEnd() != unit.getContentLength()) {
			// TODO: Handle
			throw new IllegalStateException("Could not parse " + unit.getFileName() + ":" + syntacticCompilationUnit.getContentLength() + "/" + unit.getContentLength() + " parsed, from " + syntacticCompilationUnit.getContentStart() + " to " + syntacticCompilationUnit.getContentEnd());
		}

		unit.setSyntacticTree(new SyntacticTree(syntacticCompilationUnit));
	}

	private void processDeclarations(CompilationUnit unit) {
		SyntacticTreeNode tree = unit.getSyntacticTree().getRoot();

		// Get OrdinaryCompilationUnit or ModularCompilationUnit
		tree = tree.get();

		// Discard modular compilation unit
		if (tree.getName().equals("ModularCompilationUnit")) {
			// TODO:
			return;
		}

		// Ensure that the compilation unit is an ordinary compilation unit
		if (!tree.getName().equals("OrdinaryCompilationUnit")) {
			// TODO:
			throw new IllegalStateException("Unknown element encountered. Found " + tree.getName() + ", expected OrdinaryCompilationUnit");
		}

		// Package declaration
		SyntacticTreeNode packageDeclarationNode = tree.get("[PackageDeclaration]").get();
		if (packageDeclarationNode != null) processPackageDeclaration(unit, packageDeclarationNode);

		// Import declaration
		SyntacticTreeNode importDeclarationsNode = tree.get("{ImportDeclaration}");
		for (SyntacticTreeNode importDeclarationNode : importDeclarationsNode.getAll()) {
			processImportDeclaration(unit, importDeclarationNode);
		}

		// Type declarations
		SyntacticTreeNode typeDeclarationsNode = tree.get("{TypeDeclaration}");
		for (SyntacticTreeNode typeDeclarationNode : typeDeclarationsNode.getAll()) {
			processTypeDeclaration(unit, typeDeclarationNode, unit);
		}
	}

	private void processPackageDeclaration(CompilationUnit unit, SyntacticTreeNode node) {
		String packageName = node.get("PackageName").getContent();
		unit.setPackage(packageName);
	}

	// SingleTypeImportDeclaration | TypeImportOnDemandDeclaration | SingleStaticImportDeclaration | StaticImportOnDemandDeclaration
	private void processImportDeclaration(CompilationUnit unit, SyntacticTreeNode node) {
		// Descend to specific import type node (SingleTypeImportDeclaration, TypeImportOnDemandDeclaration, etc)
		node = node.get();

		String packageOrTypeName = node.get("PackageOrTypeName").getContent();

		switch (node.getName()) {
			case "SingleTypeImportDeclaration" -> {
				String simpleClassName = node.get("TypeIdentifier").getContent();
				String canonicalName = packageOrTypeName + "." + simpleClassName;

				unit.addSingleTypeImport(simpleClassName, canonicalName);
			}
			case "TypeImportOnDemandDeclaration" -> unit.addOnDemandTypeImport(packageOrTypeName);
			case "SingleStaticImportDeclaration" -> {
				String simpleClassName = node.get("TypeIdentifier").getContent();
				String canonicalName = packageOrTypeName + "." + simpleClassName;
				String staticMember = node.get("Identifier").getContent();

				unit.addSingleStaticImport(staticMember, canonicalName);
			}
			case "StaticImportOnDemandDeclaration" -> {
				String simpleClassName = node.get("TypeIdentifier").getContent();
				String canonicalName = packageOrTypeName + "." + simpleClassName;

				unit.addOnDemandStaticImport(canonicalName);
			}
		}
	}

	private void processTypeDeclaration(CompilationUnit unit, SyntacticTreeNode node, Scope scope) {
		// Descend to specific type declaration type node (ClassDeclaration, ImportDeclaration)
		node = node.get();

		switch (node.getName()) {
			case "';'" -> {}
			case "ClassDeclaration" -> processClassDeclaration(unit, node, scope);
			case "InterfaceDeclaration" -> processInterfaceDeclaration(unit, node, scope);
			default -> throw new IllegalStateException("Unexpected " + node.getName()); // TODO
		}
	}

	// NormalClassDeclaration | EnumDeclaration
	private void processClassDeclaration(CompilationUnit unit, SyntacticTreeNode node, Scope scope) {
		// Descend to specific class declaration type node (NormalClassDeclaration, EnumDeclaration)
		node = node.get();

		boolean normalOrEnum = switch(node.getName()) {
			case "NormalClassDeclaration" -> true;
			case "EnumDeclaration" -> false;
			default -> throw new IllegalStateException("Unexpected " + node.getName()); // TODO
		};

		String name = node.get("TypeIdentifier").getContent();
		int modifiers = parseModifiers(node.get("{ClassModifier}"));
		List<String> superInterfaces = parseInterfaces(node.get("[Superinterfaces]"));

		if (normalOrEnum) {
			String superClass = Object.class.getName();
			SyntacticTreeNode superClassNode = node.get("[Superclass]").get();
			if (superClassNode != null) {
				superClass = superClassNode.get("ClassType").getContent();
			}

			CWClass cwClass = new CWClass(scope, unit.getPackage(), modifiers, name, superClass, superInterfaces);

			processTypeParameters(node.get("[TypeParameters]"), cwClass);

			SyntacticTreeNode classBodyDeclarations = node.get("ClassBody").get("{ClassBodyDeclaration}");
			for (SyntacticTreeNode classBodyDeclaration : classBodyDeclarations.getAll()) {
				processClassBodyDeclaration(unit, classBodyDeclaration, cwClass);
			}

			mParsedSourceTree.addType(cwClass);
		} else {
			CWEnum cwEnum = new CWEnum(scope, unit.getPackage(), modifiers, name, superInterfaces);

			SyntacticTreeNode enumBody = node.get("EnumBody");
			SyntacticTreeNode enumConstantList = enumBody.get("[EnumConstantList]").get();
			if (enumConstantList != null) {
				for (SyntacticTreeNode enumConstant : enumConstantList.getListElements()) {
					cwEnum.addConstant(enumConstant.get("Identifier").getContent());
				}
			}

			SyntacticTreeNode enumBodyDeclarations = enumBody.get("[EnumBodyDeclarations]").get();
			if (enumBodyDeclarations != null) {
				SyntacticTreeNode classBodyDeclarations = enumBodyDeclarations.get("{ClassBodyDeclaration}");
				for (SyntacticTreeNode classBodyDeclaration : classBodyDeclarations.getAll()) {
					processClassBodyDeclaration(unit, classBodyDeclaration, cwEnum);
				}
			}

			mParsedSourceTree.addType(cwEnum);
		}
	}

	// NormalInterfaceDeclaration | AnnotationTypeDeclaration
	private void processInterfaceDeclaration(CompilationUnit unit, SyntacticTreeNode node, Scope scope) {
		// Descend to specific interface declaration type node (NormalInterfaceDeclaration, AnnotationTypeDeclaration)
		node = node.get();

		switch (node.getName()) {
			case "NormalInterfaceDeclaration" -> processNormalInterfaceDeclaration(unit, node, scope);
			case "AnnotationTypeDeclaration" -> processAnnotationTypeDeclaration(unit, node, scope);
			default -> throw new IllegalStateException("Unexpected " + node.getName()); // TODO
		}
	}

	// {InterfaceModifier} 'interface' TypeIdentifier [TypeParameters] [ExtendsInterfaces] InterfaceBody
	private void processNormalInterfaceDeclaration(CompilationUnit unit, SyntacticTreeNode node, Scope scope) {
		String name = node.get("TypeIdentifier").getContent();
		int modifiers = parseModifiers(node.get("{InterfaceModifier}"));
		List<String> extendsInterfaces = parseInterfaces(node.get("[ExtendsInterfaces]"));

		CWInterface cwInterface = new CWInterface(scope, unit.getPackage(), modifiers, name, extendsInterfaces);

		processTypeParameters(node.get("[TypeParameters]"), cwInterface);

		SyntacticTreeNode classBodyDeclarations = node.get("InterfaceBody").get("{InterfaceMemberDeclaration}");
		for (SyntacticTreeNode classBodyDeclaration : classBodyDeclarations.getAll()) {
			processMemberDeclaration(unit, classBodyDeclaration, cwInterface);
		}

		mParsedSourceTree.addType(cwInterface);
	}

	// {InterfaceModifier} '@' 'interface' TypeIdentifier AnnotationTypeBody
	private void processAnnotationTypeDeclaration(CompilationUnit unit, SyntacticTreeNode node, Scope scope) {
		throw new IllegalStateException("@Annotations not supported");
	}

	// ClassMemberDeclaration | InstanceInitializer | StaticInitializer | ConstructorDeclaration
	private void processClassBodyDeclaration(CompilationUnit unit, SyntacticTreeNode node, CWClassOrInterface classOrInterface) {
		// Descend to specific class body declaration type node (ClassMemberDeclaration, InstanceInitializer, StaticInitializer, ConstructorDeclaration)
		node = node.get();

		// TODO:
		switch (node.getName()) {
			case "ClassMemberDeclaration" -> processMemberDeclaration(unit, node, classOrInterface);
			case "InstanceInitializer", "StaticInitializer" -> processInitializerDeclaration(node, classOrInterface);
			case "ConstructorDeclaration" -> processConstructorDeclaration(node, classOrInterface);
			default -> throw new IllegalStateException("Unexpected " + node.getName()); // TODO
		}
	}

	// {ConstructorModifier} [TypeParameters] SimpleTypeName '(' [ReceiverParameter ','] [FormalParameterList] ')' [Throws] ConstructorBody
	private void processConstructorDeclaration(SyntacticTreeNode node, CWClassOrInterface classOrInterface) {
		int modifiers = parseModifiers(node.get("{ConstructorModifier}"));
		String name = node.get("TypeIdentifier").getContent();

		if (!name.equals(classOrInterface.getSimpleName())) {
			// TODO:
			throw new IllegalStateException("Unexpected constructor " + name + " in " + classOrInterface.getSimpleName());
		}

		CWConstructor cwConstructor = new CWConstructor(classOrInterface, modifiers);
		classOrInterface.addConstructor(cwConstructor);

		processTypeParameters(node.get("[TypeParameters]"), cwConstructor);
		processFormalParameterList(node.get("[FormalParameterList]"), cwConstructor);
	}

	// FieldDeclaration | ConstantDeclaration | MethodDeclaration | InterfaceMethodDeclaration | ClassDeclaration | InterfaceDeclaration
	private void processMemberDeclaration(CompilationUnit unit, SyntacticTreeNode node, CWClassOrInterface scope) {
		// Descend to specific member declaration type node (FieldDeclaration, ConstantDeclaration, MethodDeclaration, InterfaceMethodDeclaration, ClassDeclaration, InterfaceDeclaration)
		node = node.get();

		// TODO:
		switch (node.getName()) {
			case "';'" -> {}
			case "FieldDeclaration", "ConstantDeclaration" -> processFieldDeclaration(node, scope);
			case "MethodDeclaration", "InterfaceMethodDeclaration" -> processMethodDeclaration(node, scope);
			case "ClassDeclaration" -> processClassDeclaration(unit, node, scope);
			case "InterfaceDeclaration" -> processInterfaceDeclaration(unit, node, scope);
			default -> throw new IllegalStateException("Unexpected " + node.getName());
		}
	}

	// FieldDeclaration | ConstantDeclaration
	private void processFieldDeclaration(SyntacticTreeNode node, CWClassOrInterface classOrInterface) {
		boolean fieldOrConstant = switch(node.getName()) {
			case "FieldDeclaration" -> true;
			case "ConstantDeclaration" -> false;
			default -> throw new IllegalStateException("Unexpected " + node.getName()); // TODO
		};

		String type = node.get("Type").getContent();
		int modifiers = parseModifiers(node.get(fieldOrConstant ? "{FieldModifier}" : "{ConstantModifier}"));

		for (SyntacticTreeNode variableDeclarator : node.get("VariableDeclaratorList").getListElements()) {
			SyntacticTreeNode variableDeclaratorId = variableDeclarator.get("VariableDeclaratorId");
			String name = variableDeclaratorId.get("Identifier").getContent();

			// TODO: Initializer

			CWField cwField = new CWField(classOrInterface, modifiers,
					type + variableDeclaratorId.get("[Dims]").getContent(), name);
			classOrInterface.addField(cwField);
		}
	}

	// {MethodModifier} [TypeParameters] {Annotation} Result Identifier '(' [ReceiverParameter ','] [FormalParameterList] ')' [Dims] [Throws] MethodBody
	private void processMethodDeclaration(SyntacticTreeNode node, CWClassOrInterface classOrInterface) {
		boolean classOrInterfaceMethod = switch(node.getName()) {
			case "MethodDeclaration" -> true;
			case "InterfaceMethodDeclaration" -> false;
			default -> throw new IllegalStateException("Unexpected " + node.getName()); // TODO
		};

		int modifiers = parseModifiers(node.get(classOrInterfaceMethod ? "{MethodModifier}" : "{InterfaceMethodModifier}"));
		String name = node.get("Identifier").getContent();
		String returnType = node.get("Result").getContent() + node.get("[Dims]").getContent();

		CWMethod cwMethod = new CWMethod(classOrInterface, modifiers, name, returnType);
		classOrInterface.addMethod(cwMethod);

		processTypeParameters(node.get("[TypeParameters]"), cwMethod);
		processFormalParameterList(node.get("[FormalParameterList]"), cwMethod);
	}

	private void processTypeParameters(SyntacticTreeNode node, CWParameterizable parameterizable) {
		// Descend into optional
		node = node.get();

		// No type parameters
		if (node == null) {
			return;
		}

		SyntacticTreeNode typeParameterList = node.get("TypeParameterList");
		for (SyntacticTreeNode typeParameter : typeParameterList.getListElements()) {
			String name = typeParameter.get("TypeIdentifier").getContent();

			List<String> bounds = new ArrayList<>();

			SyntacticTreeNode typeBounds = typeParameter.get("[TypeBounds]").get();
			if (typeBounds != null) {
				SyntacticTreeNode typeBoundsList = typeBounds.get("TypeBoundsList");
				for (SyntacticTreeNode typeBound : typeBoundsList.getListElements()) {
					bounds.add(typeBound.getContent());
				}
			}

			parameterizable.addTypeParameter(new CWTypeParameter((Scope) parameterizable, name, bounds));
		}
	}

	private void processFormalParameterList(SyntacticTreeNode node, CWConstructorOrMethod constructorOrMethod) {
		// Descend into optional
		node = node.get();

		// No parameters
		if (node == null) {
			return;
		}

		for (SyntacticTreeNode formalParameter : node.getListElements()) {
			boolean arityParameter = false;
			if (formalParameter.getElementCount() == 1 && formalParameter.get().getName().equals("VariableArityParameter")) {
				formalParameter = formalParameter.get();
				arityParameter = true;
			}

			int modifiers = parseModifiers(formalParameter.get("{VariableModifier}"));
			String type = formalParameter.get("Type").getContent();
			String name;
			if (arityParameter) {
				name = formalParameter.get("Identifier").getContent();
				type += "...";
			} else {
				SyntacticTreeNode variableDeclaratorId = formalParameter.get("VariableDeclaratorId");
				name = variableDeclaratorId.get("Identifier").getContent();
				type += variableDeclaratorId.get("[Dims]").getContent();
			}

			constructorOrMethod.addParameter(new CWVariable((Scope) constructorOrMethod, modifiers, type, name));
		}
	}

	private void processInitializerDeclaration(SyntacticTreeNode node, CWClassOrInterface scope) {
		boolean staticOrInstance = switch (node.getName()) {
			case "StaticInitializer" -> true;
			case "InstanceInitializer" -> false;
			default -> throw new IllegalStateException("Unexpected " + node.getName()); // TODO
		};

		// TODO: BLOCK
		scope.addInitializer(new CWInitializer(scope, staticOrInstance));
	}

	private List<String> parseInterfaces(SyntacticTreeNode node) {
		// Descend into optional
		node = node.get();

		if (node == null) {
			return Collections.emptyList();
		}

		List<String> superInterfaces = new ArrayList<>();

		SyntacticTreeNode interfaceTypeList = node.get("InterfaceTypeList");
		// TODO: PARSE

		return superInterfaces;
	}

	// TODO:
	private List<String> parseTypeList(SyntacticTreeNode node) {
		return null;
	}

	private int parseModifiers(SyntacticTreeNode modifiersNode) {


		int modifiers = 0;
		for (SyntacticTreeNode modifierNode : modifiersNode.getAll()) {
			modifierNode = modifierNode.get();

			// Ignore annotations
			if (modifierNode.getName().equals("Annotation")) {
				// TODO: Do something?
				continue;
			}

			String modifier = modifierNode.getContent();
			if (MODIFIERS.containsKey(modifier)) {
				modifiers |= MODIFIERS.get(modifierNode.getContent());
			} else {
				// TODO:
				throw new IllegalStateException("Unexpected element encountered. Found " + modifier + ", expected a modifier");
			}
		}

		return modifiers;
	}
}
