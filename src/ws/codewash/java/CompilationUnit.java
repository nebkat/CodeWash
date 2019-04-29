package ws.codewash.java;

import ws.codewash.parser.input.InputElement;
import ws.codewash.parser.input.Token;
import ws.codewash.parser.tree.LexicalTree;
import ws.codewash.parser.tree.SyntacticTree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Java Compilation Unit
 *
 * Class to represent a Java compilation unit (typically a file), containing an optional package declaration, imports
 * and class definitions. The compilation unit is the base for parsing Java source code.
 */
public class CompilationUnit extends Scope {
	/** Path to the source file of the compilation unit */
	private final Path mPath;

	/** Source content */
	private String mContent;

	// Parse trees
	private LexicalTree mLexicalTree;
	private SyntacticTree mSyntacticTree;

	/** Java input tokens */
	private List<Token> mTokens;
	/** Jave input elements */
	private List<InputElement> mInputElements;

	/** Package the compilation unit is in, defined in the package declaration at the top of the file */
	private CWPackage mPackage;

	/*
	 * Type imports
	 */
	private Map<String, RawType> mTypeImportsSingle = new HashMap<>();
	private List<RawType> mTypeImportsOnDemand = new ArrayList<>();

	/*
	 * Static field/method imports
	 */
	private Map<String, RawType> mStaticImportsSingle = new HashMap<>();
	private List<RawType> mStaticImportsOnDemand = new ArrayList<>();

	/**
	 * Constructs a new {@code CompilationUnit} for the given file
	 *
	 * @param path source file
	 * @throws IOException File read failure
	 */
	public CompilationUnit(Path path) throws IOException {
		super();

		mPath = path;
		mContent = new String(Files.readAllBytes(path)).trim();
	}

	/**
	 * Returns the Path of the source file
	 *
	 * @return Path of the source file
	 */
	public Path getPath() {
		return mPath;
	}

	/**
	 * Returns the filename of the source file
	 *
	 * @return Filename of the source file
	 */
	public String getFileName() {
		return mPath.getFileName().toString();
	}

	/**
	 * Returns the content of the source code file
	 *
	 * @return content of the file
	 */
	public String getContent() {
		return mContent;
	}

	/**
	 * Returns the source code file length
	 *
	 * @return file length
	 */
	public int getContentLength() {
		return mContent.length();
	}

	/**
	 * Sets the source code content after performing Unicode translations
	 *
	 * @param content source code content
	 */
	public void setContent(String content) {
		mContent = content;
	}

	/**
	 * Sets the parsed lexical tree of the compilation unit
	 *
	 * @param tree parsed lexical tree
	 */
	public void setLexicalTree(LexicalTree tree) {
		mLexicalTree = tree;
	}

	/**
	 * Sets the parsed syntactic tree of the compilation unit
	 *
	 * @param tree parsed syntactic tree
	 */
	public void setSyntacticTree(SyntacticTree tree) {
		mSyntacticTree = tree;
	}

	/**
	 * Returns the list of {@link InputElement}s of the compilation unit
	 *
	 * @return list of {@code InputElement}s
	 */
	public List<InputElement> getInputElements() {
		return mInputElements;
	}

	/**
	 * Sets the parsed input elements of the compilation unit
	 *
	 * The input elements are extracted from the parsed lexical tree.
	 * All {@link Token}s are filtered and put in the tokens list.
	 *
	 * @param inputElements list of input elements
	 */
	public void setInputElements(List<InputElement> inputElements) {
		mInputElements = inputElements;

		// Filter all tokens and set to tokens list
		mTokens = inputElements.stream()
				.filter(Token.class::isInstance)
				.map(Token.class::cast)
				.collect(Collectors.toList());
	}

	/**
	 * Returns the list of {@link Token}s contained in the compilation unit
	 *
	 * @return list of {@code Token}s
	 */
	public List<Token> getTokens() {
		return mTokens;
	}

	/**
	 * Returns the parsed lexical tree of the compilation unit
	 *
	 * @return parsed lexical tree
	 */
	public LexicalTree getLexicalTree() {
		return mLexicalTree;
	}

	/**
	 * Returns the parsed syntactic tree of the compilation unit
	 *
	 * @return parsed syntactic tree
	 */
	public SyntacticTree getSyntacticTree() {
		return mSyntacticTree;
	}

	/**
	 * Returns a substring of the source code content between the provided input element indices
	 *
	 * @param startElement start input element index
	 * @param endElement end input element index
	 * @return substring of content between {@code startElement} and {@code endElement}
	 */
	public String getContentRange(int startElement, int endElement) {
		return mContent.substring(mInputElements.get(startElement).getStart(), mInputElements.get(endElement).getEnd());
	}

	/**
	 * Adds a new type declaration to the compilation unit scope, as well its parent scope
	 *
	 * @param type type declaration
	 */
	@Override
	public void addTypeDeclaration(CWType type) {
		super.addTypeDeclaration(type);
		mPackage.addTypeDeclaration(type);
	}

	/**
	 * Adds a single type import to the list of imports
	 *
	 * @param canonicalName canonical name of the import
	 */
	public void addSingleTypeImport(RawType canonicalName) {
		String simpleName = canonicalName.getLast().getIdentifier();
		// Check if simple name is already imported
		if (mTypeImportsSingle.containsKey(simpleName)) {
			// Check if previous import is the same as new import
			if (!mTypeImportsSingle.get(simpleName).equals(canonicalName)) {
				// TODO:
				throw new IllegalArgumentException("Duplicate type import for " + simpleName + ". Previous declaration: " + mTypeImportsSingle.get(simpleName));
			} else {
				return;
			}
		}

		mTypeImportsSingle.put(simpleName, canonicalName);

		resolve(new PendingType<>(canonicalName, super::addTypeDeclaration));
	}

	/**
	 * Adds a wildcard/on demand type import to the list of imports
	 *
	 * @param canonicalName canonical name of the import
	 */
	public void addOnDemandTypeImport(RawType canonicalName) {
		mTypeImportsOnDemand.add(canonicalName);
	}

	/**
	 * Adds a single static method/field import to the list of imports
	 *
	 * @param identifier the static method/field identifier
	 * @param canonicalName canonical name in which the static method/field is found
	 */
	public void addSingleStaticImport(String identifier, RawType canonicalName) {
		// TODO: Duplicates
		mStaticImportsSingle.put(identifier, canonicalName);
	}

	/**
	 * Adds a wildcard/on demand static method/field import to the list of imports
	 *
	 * @param canonicalName canonical name of the import
	 */
	public void addOnDemandStaticImport(RawType canonicalName) {
		mStaticImportsOnDemand.add(canonicalName);
	}

	/**
	 * Returns the package the compilation unit is in
	 *
	 * @return the compilation unit package
	 */
	public CWPackage getPackage() {
		return mPackage;
	}

	/**
	 * Sets the compilation unit package once the package declaration has been parsed
	 *
	 * @param cwPackage the compilation unit package
	 */
	public void setPackage(CWPackage cwPackage) {
		mPackage = cwPackage;
		mEnclosingScope = cwPackage;
		mEnclosingScope.addChild(this);
	}

	/**
	 * Resolves a type by looking in own type declarations and imports
	 *
	 * @param identifier identifier to look for
	 * @param startScope the scope in which the search for this identifier started
	 * @return resolved type or {@code null} if not found
	 */
	@Override
	CWType resolveUpwards(RawType.Identifier identifier, Scope startScope) {
		if (getTypeDeclaration(identifier.getIdentifier()) == null) {
			for (RawType rawType : mTypeImportsOnDemand) {
				CWType resolvedType = getRoot().getOrInitPackage(rawType.toString()).resolveUpwards(identifier, startScope);
				if (resolvedType != null) {
					if (resolvedType instanceof CWParameterizedType) {
						resolvedType = ((CWParameterizedType) resolvedType).getType();
					}
					addTypeDeclaration(resolvedType);
				}
			}
		}

		return super.resolveUpwards(identifier, startScope);
	}

	@Override
	public String toString() {
		return mPath.toString().replace('\\', '/');
	}
}
