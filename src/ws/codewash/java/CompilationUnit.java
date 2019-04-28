package ws.codewash.parser;

import ws.codewash.java.CWPackage;
import ws.codewash.java.CWType;
import ws.codewash.java.PendingType;
import ws.codewash.java.RawType;
import ws.codewash.java.Scope;
import ws.codewash.parser.tree.LexicalTree;
import ws.codewash.parser.tree.SyntacticTree;
import ws.codewash.util.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompilationUnit extends Scope {
	private final Path mPath;

	private String mContent;

	private LexicalTree mLexicalTree;
	private SyntacticTree mSyntacticTree;

	private List<Token> mTokens;

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

	void setContent(String content) {
		mContent = content;
	}

	void setLexicalTree(LexicalTree tree) {
		mLexicalTree = tree;
	}

	void setSyntacticTree(SyntacticTree tree) {
		mSyntacticTree = tree;
	}

	void setTokens(List<Token> tokens) {
		mTokens = tokens;
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

	@Override
	public void addTypeDeclaration(CWType type) {
		super.addTypeDeclaration(type);
		mPackage.addTypeDeclaration(type);
	}

	void addSingleTypeImport(RawType canonicalName) {
		String simpleName = canonicalName.getLast().getIdentifier();
		// Check if simple name is already imported
		if (mTypeImportsSingle.containsKey(simpleName)) {
			// Check if previous import is the same as new import
			if (!mTypeImportsSingle.get(simpleName).equals(canonicalName)) {
				//throw new RedeclarationParseException("Duplicate type import for " + simpleName + ". Previous declaration: " + mTypeImportsSingle.get(simpleName), this, sourceOffset);
				// TODO:
				throw new IllegalArgumentException("Duplicate type import for " + simpleName + ". Previous declaration: " + mTypeImportsSingle.get(simpleName));
			} else {
				return;
			}
		}

		mTypeImportsSingle.put(simpleName, canonicalName);

		resolve(new PendingType<>(canonicalName, super::addTypeDeclaration));
	}

	void addOnDemandTypeImport(RawType packageName) {
		mTypeImportsOnDemand.add(packageName);
	}

	void addSingleStaticImport(String identifier, RawType canonicalName) {
		// TODO: Duplicates
		mStaticImportsSingle.put(identifier, canonicalName);
	}

	void addOnDemandStaticImport(RawType canonicalName) {
		mStaticImportsOnDemand.add(canonicalName);
	}

	public CWPackage getPackage() {
		return mPackage;
	}

	public String getPackageName() {
		return mPackage.getName();
	}

	void setPackage(CWPackage cwPackage) {
		mPackage = cwPackage;
		mEnclosingScope = cwPackage;
	}

	@Override
	public CWType resolveOutwards(RawType type, Scope startScope) {
		/*String identifier = type.getFirst().getIdentifier();
		if (mTypeImportsSingle.containsKey(identifier)) {
			RawType resolvedType = mTypeImportsSingle.get(identifier);

		}*/
		return super.resolveOutwards(type, startScope);
	}

	@Override
	public String toString() {
		return mPath.toString();
	}
}
