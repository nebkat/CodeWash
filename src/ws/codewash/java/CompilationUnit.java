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

public class CompilationUnit extends Scope {
	private final Path mPath;

	private String mContent;

	private LexicalTree mLexicalTree;
	private SyntacticTree mSyntacticTree;

	private List<Token> mTokens;
	private List<InputElement> mInputElements;

	private CWPackage mPackage;

	private Map<String, RawType> mTypeImportsSingle = new HashMap<>();
	private List<RawType> mTypeImportsOnDemand = new ArrayList<>();

	private Map<String, RawType> mStaticImportsSingle = new HashMap<>();
	private List<RawType> mStaticImportsOnDemand = new ArrayList<>();

	public CompilationUnit(Scope scope, Path path) throws IOException {
		super(scope);

		mPath = path;
		mContent = new String(Files.readAllBytes(path)).trim();
	}

	public Path getPath() {
		return mPath;
	}

	public String getFileName() {
		return mPath.getFileName().toString();
	}

	public String getContent() {
		return mContent;
	}

	public int getContentLength() {
		return mContent.length();
	}

	public void setContent(String content) {
		mContent = content;
	}

	public void setLexicalTree(LexicalTree tree) {
		mLexicalTree = tree;
	}

	public void setSyntacticTree(SyntacticTree tree) {
		mSyntacticTree = tree;
	}

	public void setInputElements(List<InputElement> inputElements) {
		mInputElements = inputElements;
		mTokens = inputElements.stream()
				.filter(Token.class::isInstance)
				.map(Token.class::cast)
				.collect(Collectors.toList());
	}

	public List<Token> getTokens() {
		return mTokens;
	}

	public LexicalTree getLexicalTree() {
		return mLexicalTree;
	}

	public SyntacticTree getSyntacticTree() {
		return mSyntacticTree;
	}

	public String getContentRange(int startElement, int endElement) {
		return mContent.substring(mInputElements.get(startElement).getStart(), mInputElements.get(endElement).getEnd());
	}

	@Override
	public void addTypeDeclaration(CWType type) {
		super.addTypeDeclaration(type);
		mPackage.addTypeDeclaration(type);
	}

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

	public void addOnDemandTypeImport(RawType packageName) {
		mTypeImportsOnDemand.add(packageName);
	}

	public void addSingleStaticImport(String identifier, RawType canonicalName) {
		// TODO: Duplicates
		mStaticImportsSingle.put(identifier, canonicalName);
	}

	public void addOnDemandStaticImport(RawType canonicalName) {
		mStaticImportsOnDemand.add(canonicalName);
	}

	public CWPackage getPackage() {
		return mPackage;
	}

	public void setPackage(CWPackage cwPackage) {
		mPackage = cwPackage;
		mEnclosingScope = cwPackage;
	}

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
