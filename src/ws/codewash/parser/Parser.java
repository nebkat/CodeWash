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
import ws.codewash.java.CompilationUnit;
import ws.codewash.java.ParsedSourceTree;
import ws.codewash.java.RawType;
import ws.codewash.java.Scope;
import ws.codewash.java.statement.CWAssertStatement;
import ws.codewash.java.statement.CWEnhancedSwitchStatement;
import ws.codewash.java.statement.CWForStatement;
import ws.codewash.java.statement.CWBlock;
import ws.codewash.java.statement.CWBreakStatement;
import ws.codewash.java.statement.CWCatchStatement;
import ws.codewash.java.statement.CWContinueStatement;
import ws.codewash.java.statement.CWControlStatement;
import ws.codewash.java.statement.CWEmptyStatement;
import ws.codewash.java.statement.CWEnhancedForStatement;
import ws.codewash.java.statement.CWIfStatement;
import ws.codewash.java.statement.CWLabeledStatement;
import ws.codewash.java.statement.CWLocalVariableDeclarationStatement;
import ws.codewash.java.statement.CWReturnStatement;
import ws.codewash.java.statement.CWStatement;
import ws.codewash.java.statement.CWSwitchStatement;
import ws.codewash.java.statement.CWSynchronizedStatement;
import ws.codewash.java.statement.CWThrowStatement;
import ws.codewash.java.statement.CWTryStatement;
import ws.codewash.java.statement.CWWhileStatement;
import ws.codewash.java.statement.expression.CWExpression;
import ws.codewash.java.statement.expression.CWUnknownExpression;
import ws.codewash.parser.grammar.Grammar;
import ws.codewash.parser.input.BooleanLiteral;
import ws.codewash.parser.input.CharacterLiteral;
import ws.codewash.parser.input.Comment;
import ws.codewash.parser.input.DoubleLiteral;
import ws.codewash.parser.input.FloatLiteral;
import ws.codewash.parser.input.Identifier;
import ws.codewash.parser.input.InputElement;
import ws.codewash.parser.input.IntegerLiteral;
import ws.codewash.parser.input.Keyword;
import ws.codewash.parser.input.LongLiteral;
import ws.codewash.parser.input.NullLiteral;
import ws.codewash.parser.input.Operator;
import ws.codewash.parser.input.Separator;
import ws.codewash.parser.input.StringLiteral;
import ws.codewash.parser.input.Token;
import ws.codewash.parser.input.WhiteSpace;
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
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ws.codewash.util.Timing.duration;
import static ws.codewash.util.Timing.time;

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

		// Resolve pending types
		start = time();
		mParsedSourceTree.resolvePendingTypes();
		Log.d(TAG, "Resolved pending types in " + duration(start, time()) + "s");

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


		List<InputElement> inputElements = new ArrayList<>();
		for (LexicalTreeNode inputElement : lexicalInput.get("{InputElement}").getAll()) {
			LexicalTreeNode originalInputElement = inputElement;

			// Descend to specific input element type (Token, Comment, WhiteSpace)
			inputElement = inputElement.get();

			inputElements.add(switch (inputElement.getName()) {
				case "Token" -> {
					LexicalTreeNode token = inputElement.get();
					break switch (token.getName()) {
						case "Identifier" -> new Identifier(originalInputElement);
						case "Keyword" -> new Keyword(originalInputElement);
						case "Literal" -> {
							LexicalTreeNode literal = token.get();
							break switch (literal.getName()) {
								case "IntegerLiteral" -> {
									LexicalTreeNode integerLiteral = literal.get();

									String integerTypeSuffix = integerLiteral.get("[IntegerTypeSuffix]").getContent();
									if (integerTypeSuffix.equalsIgnoreCase("l")) {
										break new LongLiteral(originalInputElement);
									} else {
										break new IntegerLiteral(originalInputElement);
									}
								}
								case "FloatingPointLiteral" -> {
									LexicalTreeNode floatingPointLiteral = literal.get();

									LexicalTreeNode floatTypeSuffixNode = floatingPointLiteral.get("[FloatTypeSuffix]");
									if (floatTypeSuffixNode == null) floatTypeSuffixNode = floatingPointLiteral.get("FloatTypeSuffix");
									String floatTypeSuffix = floatTypeSuffixNode.getContent();
									if (floatTypeSuffix.equalsIgnoreCase("f")) {
										break new FloatLiteral(originalInputElement);
									} else {
										break new DoubleLiteral(originalInputElement);
									}
								}
								case "BooleanLiteral" -> new BooleanLiteral(originalInputElement);
								case "CharacterLiteral" -> new CharacterLiteral(originalInputElement);
								case "StringLiteral" -> new StringLiteral(originalInputElement);
								case "NullLiteral" -> new NullLiteral(originalInputElement);
								default -> throw new IllegalStateException("Unexpected " + literal.getName());
							};
						}
						case "Separator" -> new Separator(originalInputElement);
						case "Operator" -> new Operator(originalInputElement);
						default -> throw new IllegalStateException("Unexpected " + token.getName());
					};
				}
				case "Comment" -> new Comment(originalInputElement);
				case "WhiteSpace" -> new WhiteSpace(originalInputElement);
				default -> throw new IllegalStateException("Unexpected " + inputElement.getName());
			});
		}

		unit.setInputElements(inputElements);
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
		processPackageDeclaration(unit, packageDeclarationNode);

		// Import declaration
		SyntacticTreeNode importDeclarationsNode = tree.get("{ImportDeclaration}");
		for (SyntacticTreeNode importDeclarationNode : importDeclarationsNode.getAll()) {
			processImportDeclaration(unit, importDeclarationNode);
		}

		// Type declarations
		SyntacticTreeNode typeDeclarationsNode = tree.get("{TypeDeclaration}");
		for (SyntacticTreeNode typeDeclarationNode : typeDeclarationsNode.getAll()) {
			processTypeDeclaration(typeDeclarationNode, unit);
		}
	}

	private void processPackageDeclaration(CompilationUnit unit, SyntacticTreeNode node) {
		String packageName = node == null ? "" : node.get("PackageName").getContent();

		unit.setPackage(mParsedSourceTree.getOrInitPackage(packageName));
	}

	// SingleTypeImportDeclaration | TypeImportOnDemandDeclaration | SingleStaticImportDeclaration | StaticImportOnDemandDeclaration
	private void processImportDeclaration(CompilationUnit unit, SyntacticTreeNode node) {
		// Descend to specific import type node (SingleTypeImportDeclaration, TypeImportOnDemandDeclaration, etc)
		node = node.get();

		// Get import canonical name
		RawType canonicalName = parseTypeName(node.get("PackageOrTypeName"));
		if (node.get("TypeIdentifier") != null) {
			canonicalName = canonicalName.append(new RawType.Identifier(node.get("TypeIdentifier").getContent()));
		}

		switch (node.getName()) {
			case "SingleTypeImportDeclaration" -> unit.addSingleTypeImport(canonicalName);
			case "TypeImportOnDemandDeclaration" -> unit.addOnDemandTypeImport(canonicalName);
			case "SingleStaticImportDeclaration" -> unit.addSingleStaticImport(node.get("Identifier").getContent(), canonicalName);
			case "StaticImportOnDemandDeclaration" -> unit.addOnDemandStaticImport(canonicalName);
			default -> throw new IllegalStateException("Unexpected " + node.getName()); // TODO
		}
	}

	private void processTypeDeclaration(SyntacticTreeNode node, Scope scope) {
		// Descend to specific type declaration type node (ClassDeclaration, ImportDeclaration)
		node = node.get();

		switch (node.getName()) {
			case "';'" -> {}
			case "ClassDeclaration" -> processClassDeclaration(node, scope);
			case "InterfaceDeclaration" -> processInterfaceDeclaration(node, scope);
			default -> throw new IllegalStateException("Unexpected " + node.getName()); // TODO
		}
	}

	// NormalClassDeclaration | EnumDeclaration
	private void processClassDeclaration(SyntacticTreeNode node, Scope scope) {
		// Descend to specific class declaration type node (NormalClassDeclaration, EnumDeclaration)
		node = node.get();

		boolean normalOrEnum = switch (node.getName()) {
			case "NormalClassDeclaration" -> true;
			case "EnumDeclaration" -> false;
			default -> throw new IllegalStateException("Unexpected " + node.getName()); // TODO
		};

		String name = node.get("TypeIdentifier").getContent();
		int modifiers = parseModifiers(node.get("{ClassModifier}"));

		List<RawType> superInterfaces = Collections.emptyList();
		SyntacticTreeNode superInterfacesNode = node.get("[Superinterfaces]").get();
		if (superInterfacesNode != null) {
			superInterfaces = parseClassTypeList(superInterfacesNode.get("InterfaceTypeList"));
		}

		if (normalOrEnum) {
			RawType superClass = RawType.OBJECT_RAW_TYPE;
			SyntacticTreeNode superClassNode = node.get("[Superclass]").get();
			if (superClassNode != null) {
				superClass = parseClassType(superClassNode.get("ClassType"), 0);
			}

			CWClass cwClass = new CWClass(scope, modifiers, name, superClass, superInterfaces);

			processTypeParameters(node.get("[TypeParameters]"), cwClass);

			SyntacticTreeNode classBodyDeclarations = node.get("ClassBody").get("{ClassBodyDeclaration}");
			for (SyntacticTreeNode classBodyDeclaration : classBodyDeclarations.getAll()) {
				processClassBodyDeclaration(classBodyDeclaration, cwClass);
			}

			mParsedSourceTree.addType(cwClass);
		} else {
			CWEnum cwEnum = new CWEnum(scope, modifiers, name, superInterfaces);

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
					processClassBodyDeclaration(classBodyDeclaration, cwEnum);
				}
			}

			mParsedSourceTree.addType(cwEnum);
		}
	}

	// NormalInterfaceDeclaration | AnnotationTypeDeclaration
	private void processInterfaceDeclaration(SyntacticTreeNode node, Scope scope) {
		// Descend to specific interface declaration type node (NormalInterfaceDeclaration, AnnotationTypeDeclaration)
		node = node.get();

		switch (node.getName()) {
			case "NormalInterfaceDeclaration" -> processNormalInterfaceDeclaration(node, scope);
			case "AnnotationTypeDeclaration" -> processAnnotationTypeDeclaration(node, scope);
			default -> throw new IllegalStateException("Unexpected " + node.getName()); // TODO
		}
	}

	// {InterfaceModifier} 'interface' TypeIdentifier [TypeParameters] [ExtendsInterfaces] InterfaceBody
	private void processNormalInterfaceDeclaration(SyntacticTreeNode node, Scope scope) {
		String name = node.get("TypeIdentifier").getContent();
		int modifiers = parseModifiers(node.get("{InterfaceModifier}"));

		List<RawType> superInterfaces = Collections.emptyList();
		SyntacticTreeNode superInterfacesNode = node.get("[ExtendsInterfaces]").get();
		if (superInterfacesNode != null) {
			superInterfaces = parseClassTypeList(superInterfacesNode.get("InterfaceTypeList"));
		}

		CWInterface cwInterface = new CWInterface(scope, modifiers, name, superInterfaces);

		processTypeParameters(node.get("[TypeParameters]"), cwInterface);

		SyntacticTreeNode classBodyDeclarations = node.get("InterfaceBody").get("{InterfaceMemberDeclaration}");
		for (SyntacticTreeNode classBodyDeclaration : classBodyDeclarations.getAll()) {
			processMemberDeclaration(classBodyDeclaration, cwInterface);
		}

		mParsedSourceTree.addType(cwInterface);
	}

	// {InterfaceModifier} '@' 'interface' TypeIdentifier AnnotationTypeBody
	private void processAnnotationTypeDeclaration(SyntacticTreeNode node, Scope scope) {
		throw new IllegalStateException("@Annotations not supported");
	}

	// ClassMemberDeclaration | InstanceInitializer | StaticInitializer | ConstructorDeclaration
	private void processClassBodyDeclaration(SyntacticTreeNode node, CWClassOrInterface classOrInterface) {
		// Descend to specific class body declaration type node (ClassMemberDeclaration, InstanceInitializer, StaticInitializer, ConstructorDeclaration)
		node = node.get();

		// TODO:
		switch (node.getName()) {
			case "ClassMemberDeclaration" -> processMemberDeclaration(node, classOrInterface);
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

		processBlock(node.get("ConstructorBody"), cwConstructor, cwConstructor::setBlock);
	}

	// FieldDeclaration | ConstantDeclaration | MethodDeclaration | InterfaceMethodDeclaration | ClassDeclaration | InterfaceDeclaration
	private void processMemberDeclaration(SyntacticTreeNode node, CWClassOrInterface scope) {
		// Descend to specific member declaration type node (FieldDeclaration, ConstantDeclaration, MethodDeclaration, InterfaceMethodDeclaration, ClassDeclaration, InterfaceDeclaration)
		node = node.get();

		// TODO:
		switch (node.getName()) {
			case "';'" -> {
			}
			case "FieldDeclaration", "ConstantDeclaration" -> processFieldDeclaration(node, scope);
			case "MethodDeclaration", "InterfaceMethodDeclaration" -> processMethodDeclaration(node, scope);
			case "ClassDeclaration" -> processClassDeclaration(node, scope);
			case "InterfaceDeclaration" -> processInterfaceDeclaration(node, scope);
			default -> throw new IllegalStateException("Unexpected " + node.getName());
		}
	}

	// FieldDeclaration | ConstantDeclaration
	private void processFieldDeclaration(SyntacticTreeNode node, CWClassOrInterface classOrInterface) {
		boolean fieldOrConstant = switch (node.getName()) {
			case "FieldDeclaration" -> true;
			case "ConstantDeclaration" -> false;
			default -> throw new IllegalStateException("Unexpected " + node.getName()); // TODO
		};

		int modifiers = parseModifiers(node.get(fieldOrConstant ? "{FieldModifier}" : "{ConstantModifier}"));

		for (SyntacticTreeNode variableDeclarator : node.get("VariableDeclaratorList").getListElements()) {
			SyntacticTreeNode variableDeclaratorId = variableDeclarator.get("VariableDeclaratorId");
			String name = variableDeclaratorId.get("Identifier").getContent();
			RawType type = parseType(node.get("Type"), parseArrayDims(variableDeclaratorId.get("[Dims]").get()));

			CWField cwField = new CWField(classOrInterface, modifiers, type, name);
			classOrInterface.addField(cwField);
		}
	}

	// {MethodModifier} [TypeParameters] {Annotation} Result Identifier '(' [ReceiverParameter ','] [FormalParameterList] ')' [Dims] [Throws] MethodBody
	private void processMethodDeclaration(SyntacticTreeNode node, CWClassOrInterface classOrInterface) {
		boolean classOrInterfaceMethod = switch (node.getName()) {
			case "MethodDeclaration" -> true;
			case "InterfaceMethodDeclaration" -> false;
			default -> throw new IllegalStateException("Unexpected " + node.getName()); // TODO
		};

		int modifiers = parseModifiers(node.get(classOrInterfaceMethod ? "{MethodModifier}" : "{InterfaceMethodModifier}"));
		String name = node.get("Identifier").getContent();

		// Descend to specific type (Type, 'void')
		SyntacticTreeNode returnTypeNode = node.get("Result").get();
		RawType returnType = parseType(returnTypeNode);

		CWMethod cwMethod = new CWMethod(classOrInterface, modifiers, name, returnType);
		classOrInterface.addMethod(cwMethod);

		processTypeParameters(node.get("[TypeParameters]"), cwMethod);
		processFormalParameterList(node.get("[FormalParameterList]"), cwMethod);

		processBlock(node.get("MethodBody").get(), cwMethod, cwMethod::setBlock);
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

			List<RawType> bounds = Collections.emptyList();

			SyntacticTreeNode typeBounds = typeParameter.get("[TypeBounds]").get();
			if (typeBounds != null) bounds = parseClassTypeList(typeBounds.get("TypeBoundsList"));

			parameterizable.addTypeParameter(new CWTypeParameter(parameterizable, name, bounds));
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
			RawType type = parseType(formalParameter.get("Type"));
			String name;
			if (arityParameter) {
				name = formalParameter.get("Identifier").getContent();
				type.setVarArgs();
			} else {
				SyntacticTreeNode variableDeclaratorId = formalParameter.get("VariableDeclaratorId");
				name = variableDeclaratorId.get("Identifier").getContent();
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

		CWInitializer cwInitializer = new CWInitializer(scope, staticOrInstance);
		scope.addInitializer(cwInitializer);

		processBlock(node.get("Block"), cwInitializer, cwInitializer::setBlock);
	}

	private void processBlock(SyntacticTreeNode node, Scope scope, Consumer<? super CWBlock> consumer) {
		switch (node.getName()) {
			case "';'": return;
			case "ConstructorBody":
			case "Block":
				break; // Parsed below
			default: throw new IllegalStateException("Unexpected " + node.getName()); // TODO
		}

		CWBlock block = new CWBlock(node, scope);
		consumer.accept(block);

		if (node.getName().equals("ConstructorBody")) {
			CWExpression expression = parseExpression(node.get("[ExplicitConstructorInvocation]"), block);
			if (expression != null) block.addStatement(expression);
		}

		SyntacticTreeNode blockStatementsNode = node.get("[BlockStatements]").get();
		processBlockStatements(blockStatementsNode, block, block::addStatement);
	}

	private void processBlockStatements(SyntacticTreeNode node, Scope scope, Consumer<CWStatement> consumer) {
		if (node == null) return;

		for (SyntacticTreeNode statementNode : node.getSimpleListElements()) {
			// Descend into specific block statement type (LocalVariableDeclarationStatement, ClassDeclaration, Statement)
			statementNode = statementNode.get();

			switch (statementNode.getName()) {
				case "LocalVariableDeclarationStatement" -> processLocalVarDeclarationStatement(statementNode, scope, consumer);
				case "ClassDeclaration" -> processClassDeclaration(node, scope);
				case "Statement" -> processStatement(statementNode, scope, consumer);
				default -> throw new IllegalStateException("Unexpected " + statementNode.getName()); // TODO:
			}
		}
	}

	private void processLocalVarDeclarationStatement(SyntacticTreeNode node, Scope scope, Consumer<? super CWLocalVariableDeclarationStatement> consumer) {
		// Descend to declaration without semicolon
		node = node.get("LocalVariableDeclaration");

		int modifiers = parseModifiers(node.get("{VariableModifier}"));

		for (SyntacticTreeNode variableDeclarator : node.get("VariableDeclaratorList").getListElements()) {
			SyntacticTreeNode variableDeclaratorId = variableDeclarator.get("VariableDeclaratorId");
			String name = variableDeclaratorId.get("Identifier").getContent();
			RawType type = parseType(node.get("LocalVariableType").get(), parseArrayDims(variableDeclaratorId.get("[Dims]").get()));

			CWVariable cwVariable = new CWVariable(scope, modifiers, type, name);
			consumer.accept(new CWLocalVariableDeclarationStatement(node, scope, cwVariable));
		}
	}

	private void processStatement(SyntacticTreeNode node, Scope scope, Consumer<CWStatement> consumer) {
		// Descend to specific statement type
		node = node.get();

		switch (node.getName()) {
			case "StatementWithoutTrailingSubstatement", "EnhancedSwitchCaseStatement" -> {
				// Descend into specific statement without trailing substatement
				node = node.get();
				switch (node.getName()) {
					case "AssertStatement" -> processAssertStatement(node, scope, consumer);
					case "Block" -> processBlock(node, scope, consumer);
					case "BreakStatement" -> processBreakStatement(node, scope, consumer);
					case "ContinueStatement" -> processContinueStatement(node, scope, consumer);
					case "DoStatement" -> processWhileStatement(node, scope, consumer);
					case "EmptyStatement" -> consumer.accept(new CWEmptyStatement(node, scope));
					case "ExpressionStatement" -> {} // TODO:
					case "ReturnStatement" -> processReturnStatement(node, scope, consumer);
					case "SwitchStatement" -> processSwitchStatement(node, scope, consumer);
					case "SynchronizedStatement" -> processSynchronizedStatement(node, scope, consumer);
					case "ThrowStatement" -> processThrowStatement(node, scope, consumer);
					case "TryStatement" -> processTryStatement(node, scope, consumer);
				}
			}
			case "LabeledStatement", "LabeledStatementNoShortIf" -> processLabeledStatement(node, scope, consumer);
			case "IfThenStatement", "IfThenElseStatement", "IfThenElseStatementNoShortIf" ->
					processIfStatement(node, scope, consumer);
			case "WhileStatement", "WhileStatementNoShortIf" -> processWhileStatement(node, scope, consumer);
			case "ForStatement", "ForStatementNoShortIf" -> processForStatement(node, scope, consumer);
		}
	}

	private void processAssertStatement(SyntacticTreeNode node, Scope scope, Consumer<? super CWAssertStatement> consumer) {
		CWExpression condition = parseExpression(node.get(1), scope);
		CWExpression detailMessageExpression = node.getElementCount() > 3 ?
				parseExpression(node.get(3), scope) : null;
		CWAssertStatement statement = new CWAssertStatement(node, scope, condition, detailMessageExpression);
		consumer.accept(statement);
	}

	private void processBreakStatement(SyntacticTreeNode node, Scope scope, Consumer<? super CWBreakStatement> consumer) {
		CWExpression expression = parseExpression(node.get("Expression"), scope);
		CWBreakStatement statement = new CWBreakStatement(node, scope, expression);
		consumer.accept(statement);
	}

	private void processContinueStatement(SyntacticTreeNode node, Scope scope, Consumer<? super CWContinueStatement> consumer) {
		CWContinueStatement statement = new CWContinueStatement(node, scope, node.get("[Identifier]").getContent());
		consumer.accept(statement);
	}

	// BasicForStatement | EnhancedForStatement
	private void processForStatement(SyntacticTreeNode node, Scope scope, Consumer<? super CWControlStatement> consumer) {
		// Descend to specific for loop type (BasicForStatement, EnhancedForStatement)
		node = node.get();

		switch (node.getName()) {
			case "BasicForStatement" -> processBasicForStatement(node, scope, consumer);
			case "EnhancedForStatement" -> processEnhancedForStatement(node, scope, consumer);
			default -> throw new IllegalStateException("Unexpected " + node.getName()); // TODO
		}
	}

	// 'for' '(' [ForInit] ';' [Expression] ';' [ForUpdate] ')' Statement
	// 'for' '(' [ForInit] ';' [Expression] ';' [ForUpdate] ')' StatementNoShortIf
	private void processBasicForStatement(SyntacticTreeNode node, Scope scope, Consumer<? super CWForStatement> consumer) {
		CWForStatement statement = new CWForStatement(node, scope);
		consumer.accept(statement);

		SyntacticTreeNode forInitNode = node.get("[ForInit]").get();
		if (forInitNode != null) {
			switch (forInitNode.get().getName()) {
				case "LocalVariableDeclaration" -> processLocalVarDeclarationStatement(forInitNode, statement, statement::setInitVariableDeclarationStatement);
				case "StatementExpressionList" -> statement.setInitExpressions(parseExpressionList(forInitNode.get(), statement));
				default -> throw new IllegalStateException("Unexpected " + forInitNode.get().getName());
			}
		}

		statement.setCondition(parseExpression(node.get("[Expression]").get(), statement));

		SyntacticTreeNode forUpdateNode = node.get("[ForUpdate]").get();
		if (forUpdateNode != null) {
			statement.setUpdateExpressions(parseExpressionList(forUpdateNode.get(), statement));
		}
	}

	// 'for' '(' {VariableModifier} LocalVariableType VariableDeclaratorId ':' Expression ')' Statement
	// 'for' '(' {VariableModifier} LocalVariableType VariableDeclaratorId ':' Expression ')' StatementNoShortIf
	private void processEnhancedForStatement(SyntacticTreeNode node, Scope scope, Consumer<? super CWEnhancedForStatement> consumer) {
		CWExpression expression = parseExpression(node.get("Expression"), scope);
		CWEnhancedForStatement statement = new CWEnhancedForStatement(node, scope, expression);
		consumer.accept(statement);

		int modifiers = parseModifiers(node.get("{VariableModifier}"));
		SyntacticTreeNode variableDeclaratorId = node.get("VariableDeclaratorId");
		String name = variableDeclaratorId.get("Identifier").getContent();
		RawType type = parseType(node.get("LocalVariableType").get());

		CWVariable cwVariable = new CWVariable(scope, modifiers, type, name);
		statement.setVariable(cwVariable);

		boolean noShortIf = node.getName().endsWith("NoShortIf");
		processStatement(node.get(!noShortIf ? "Statement" : "StatementNoShortIf"), statement, statement::setStatement);
	}

	private void processReturnStatement(SyntacticTreeNode node, Scope scope, Consumer<? super CWReturnStatement> consumer) {
		CWExpression expression = parseExpression(node.get("Expression"), scope);
		CWReturnStatement statement = new CWReturnStatement(node, scope, expression);
		consumer.accept(statement);
	}

	// BasicSwitchStatement | EnhancedSwitchStatement
	private void processSwitchStatement(SyntacticTreeNode node, Scope scope, Consumer<? super CWControlStatement> consumer) {
		// Descend to specific for loop type (BasicSwitchStatement, EnhancedSwitchStatement)
		node = node.get();

		switch (node.getName()) {
			case "BasicSwitchStatement" -> processBasicSwitchStatement(node, scope, consumer);
			case "EnhancedSwitchStatement" -> processEnhancedSwitchStatement(node, scope, consumer);
			default -> throw new IllegalStateException("Unexpected " + node.getName()); // TODO
		}
	}

	// 'switch' '(' Expression ')' '{' {SwitchBlockStatementGroup} '}'
	private void processBasicSwitchStatement(SyntacticTreeNode node, Scope scope, Consumer<? super CWSwitchStatement> consumer) {
		CWExpression expression = parseExpression(node.get("Expression"), scope);
		CWSwitchStatement statement = new CWSwitchStatement(node, scope, expression);
		consumer.accept(statement);

		SyntacticTreeNode switchBlockStatementGroups = node.get("{SwitchBlockStatementGroup}");
		for (SyntacticTreeNode switchBlockStatementGroup : switchBlockStatementGroups.getAll()) {
			CWExpression lastExpression = null;
			for (SyntacticTreeNode switchLabel : switchBlockStatementGroup.get("SwitchLabels").getSimpleListElements()) {
				CWExpression caseExpression = lastExpression = switch (switchLabel.get(0).getName()) {
					case "'case'" -> parseExpression(switchLabel.get("Expression"), statement);
					case "'default'" -> null;
					default -> throw new IllegalStateException("Unexpected " + switchLabel.get(0).getName());
				};
				statement.addCaseExpression(caseExpression);
			}
			final CWExpression finalLastExpression = lastExpression;

			processBlockStatements(switchBlockStatementGroup.get("[BlockStatements]").get(),
					statement, s -> statement.addCaseStatement(finalLastExpression, s));
		}
	}

	// 'switch' '(' Expression ')' '{' {EnhancedSwitchCase} '}'
	private void processEnhancedSwitchStatement(SyntacticTreeNode node, Scope scope, Consumer<? super CWEnhancedSwitchStatement> consumer) {
		CWExpression expression = parseExpression(node.get("Expression"), scope);
		CWEnhancedSwitchStatement statement = new CWEnhancedSwitchStatement(node, scope, expression);
		consumer.accept(statement);

		// 'case' Expression {',' Expression} '->' EnhancedSwitchCaseStatement
		// 'default' '->' EnhancedSwitchCaseStatement
		for (SyntacticTreeNode enhancedSwitchCase : node.get("{EnhancedSwitchCase}").getAll()) {
			final List<CWExpression> caseExpressions = switch (enhancedSwitchCase.get(0).getName()) {
				case "'case'" -> parseExpressionList(enhancedSwitchCase.get("ArgumentList"), statement);
				case "'default'" -> Collections.emptyList();
				default -> throw new IllegalStateException("Unexpected " + enhancedSwitchCase.get(0).getName());
			};

			processStatement(enhancedSwitchCase.get("EnhancedSwitchCaseStatement"),
					statement, s -> statement.addCaseStatement(caseExpressions, s));
		}
	}

	private void processSynchronizedStatement(SyntacticTreeNode node, Scope scope, Consumer<? super CWSynchronizedStatement> consumer) {
		CWExpression object = parseExpression(node.get("Expression"), scope);
		CWSynchronizedStatement statement = new CWSynchronizedStatement(node, scope, object);
		consumer.accept(statement);

		processBlock(node.get("Block"), statement, statement::setBlock);
	}

	private void processThrowStatement(SyntacticTreeNode node, Scope scope, Consumer<? super CWThrowStatement> consumer) {
		CWExpression expression = parseExpression(node.get("Expression"), scope);
		CWThrowStatement statement = new CWThrowStatement(node, scope, expression);
		consumer.accept(statement);
	}

	private void processTryStatement(SyntacticTreeNode node, Scope scope, Consumer<? super CWTryStatement> consumer) {
		CWTryStatement statement = new CWTryStatement(node, scope);
		consumer.accept(statement);

		SyntacticTreeNode catchesNode = node.get("Catches");
		if (catchesNode == null) catchesNode = node.get("[Catches]").get();
		if (catchesNode != null) {
			for (SyntacticTreeNode catchNode : catchesNode.getSimpleListElements()) {
				SyntacticTreeNode catchFormalParameterNode = catchNode.get("CatchFormalParameter");

				int modifiers = parseModifiers(catchFormalParameterNode.get("{VariableModifier}"));
				SyntacticTreeNode variableDeclaratorId = catchFormalParameterNode.get("VariableDeclaratorId");
				String name = variableDeclaratorId.get("Identifier").getContent();
				RawType type = parseClassType(catchFormalParameterNode.get("CatchType").get("ClassType"));

				CWVariable cwVariable = new CWVariable(scope, modifiers, type, name);
				CWCatchStatement catchStatement = new CWCatchStatement(node, statement, cwVariable);
				processBlock(catchNode.get("Block"), catchStatement, catchStatement::setBlock);
			}
		}

		SyntacticTreeNode finallyNode = node.get("Finally");
		if (finallyNode == null) {
			finallyNode = node.get("[Finally]");
			if (finallyNode != null) finallyNode = finallyNode.get();
		}
		if (finallyNode != null) {
			processBlock(finallyNode.get("Block"), statement, statement::setFinallyBlock);
		}
	}

	private void processLabeledStatement(SyntacticTreeNode node, Scope scope, Consumer<? super CWLabeledStatement> consumer) {
		String label = node.get("Identifier").getContent();
		CWLabeledStatement statement = new CWLabeledStatement(node, scope, label);
		consumer.accept(statement);

		boolean noShortIf = node.getName().endsWith("NoShortIf");
		processStatement(node.get(!noShortIf ? "Statement" : "StatementNoShortIf"), statement, statement::setStatement);
	}

	// 'if' '(' Expression ')' Statement
	// 'if' '(' Expression ')' StatementNoShortIf 'else' Statement
	// 'if' '(' Expression ')' StatementNoShortIf 'else' StatementNoShortIf
	private void processIfStatement(SyntacticTreeNode node, Scope scope, Consumer<? super CWIfStatement> consumer) {
		CWExpression condition = parseExpression(node.get("Expression"), scope);
		CWIfStatement statement = new CWIfStatement(node, scope, condition);
		consumer.accept(statement);

		switch (node.getName()) {
			case "IfThenStatement" -> processStatement(node.get(4), statement, statement::setThenStatement);
			case "IfThenElseStatement", "IfThenElseStatementNoShortIf" -> {
				processStatement(node.get(4), statement, statement::setThenStatement);
				processStatement(node.get(6), statement, statement::setElseStatement);
			}
		}
	}

	// 'do' Statement 'while' '(' Expression ')' ';'
	// 'while' '(' Expression ')' Statement
	// 'while' '(' Expression ')' StatementNoShortIf
	private void processWhileStatement(SyntacticTreeNode node, Scope scope, Consumer<? super CWWhileStatement> consumer) {
		CWExpression condition = parseExpression(node.get("Expression"), scope);
		CWWhileStatement statement = new CWWhileStatement(node, scope, node.getName().equals("DoStatement"), condition);
		consumer.accept(statement);

		boolean noShortIf = node.getName().endsWith("NoShortIf");
		processStatement(node.get(!noShortIf ? "Statement" : "StatementNoShortIf"), statement, statement::setStatement);
	}

	private CWExpression parseExpression(SyntacticTreeNode node, Scope scope) {
		if (node == null) return null;
		return new CWUnknownExpression(node, scope);
	}

	private List<CWExpression> parseExpressionList(SyntacticTreeNode node, Scope scope) {
		return node.getListElements().stream()
				.map(expression -> parseExpression(expression, scope))
				.collect(Collectors.toList());
	}

	// TODO: RawTypeName for names
	private RawType parseTypeName(SyntacticTreeNode node) {
		return new RawType(node.getListElements().stream()
				.map(SyntacticTreeNode::getContent)
				.map(RawType.Identifier::new)
				.collect(Collectors.toList()));
	}

	private RawType parseType(SyntacticTreeNode node) {
		return parseType(node, 0);
	}

	private RawType parseType(SyntacticTreeNode node, int outerArrayDimension) {
		switch (node.getName()) {
			case "'void'": return RawType.VOID_RAW_TYPE;
			case "'var'": return RawType.VAR_RAW_TYPE;
			case "Wildcard": return RawType.WILDCARD_RAW_TYPE;
			case "Type": break; // Parsed below
			default: throw new IllegalStateException("Unexpected " + node.getName());
		}

		int arrayDimension = parseArrayDims(node.get("[Dims]").get()) + outerArrayDimension;

		// Descend to specific type (ClassType, PrimitiveType)
		node = node.get(0);

		return switch (node.getName()) {
			case "ClassType" -> parseClassType(node, arrayDimension);
			case "PrimitiveType" -> parsePrimitiveType(node, arrayDimension);
			default -> throw new IllegalStateException("Unexpected " + node.getName());
		};
	}

	private int parseArrayDims(SyntacticTreeNode node) {
		// No dims present
		if (node == null) return 0;

		return 1 + node.get("{{Annotation} '[' ']'}").getElementCount();
	}

	private RawType parseClassType(SyntacticTreeNode node) {
		return parseClassType(node, 0);
	}

	private RawType parseClassType(SyntacticTreeNode node, int arrayDimension) {
		List<RawType.Identifier> identifiers = new ArrayList<>();

		for (SyntacticTreeNode identifierNode : node.getListElements()) {
			List<RawType> typeArguments = new ArrayList<>();
			SyntacticTreeNode typeArgumentsNode = identifierNode.get("[TypeArguments]").get();
			if (typeArgumentsNode != null) {
				SyntacticTreeNode typeArgumentList = typeArgumentsNode.get("TypeArgumentList");
				for (SyntacticTreeNode typeArgumentNode : typeArgumentList.getListElements()) {
					typeArguments.add(parseType(typeArgumentNode.get()));
				}
			}

			String identifier = identifierNode.get("Identifier").getContent();

			identifiers.add(new RawType.Identifier(identifier, typeArguments));
		}

		return new RawType(identifiers, arrayDimension);
	}

	private List<RawType> parseClassTypeList(SyntacticTreeNode node) {
		return node.getListElements().stream()
				.map(this::parseClassType)
				.collect(Collectors.toList());
	}

	// {Annotation} NumericType | {Annotation} 'boolean'
	private RawType parsePrimitiveType(SyntacticTreeNode node, int arrayDimension) {
		return new RawType(Collections.singletonList(new RawType.Identifier(node.get(1).getContent())), arrayDimension);
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
