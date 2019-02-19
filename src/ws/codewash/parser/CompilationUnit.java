package ws.codewash.parser;

import ws.codewash.java.Scope;
import ws.codewash.parser.tree.LexicalTree;
import ws.codewash.parser.tree.SyntacticTree;

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

	private String mPackageName = "";

	private Map<String, String> mTypeImportsSingle = new HashMap<>();
	private List<String> mTypeImportsOnDemand = new ArrayList<>();

	private Map<String, String> mStaticImportsSingle = new HashMap<>();
	private List<String> mStaticImportsOnDemand = new ArrayList<>();

	public CompilationUnit(Path path) throws IOException {
		super();

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

	void addSingleTypeImport(String simpleName, String canonicalName) {
		// Check if simple name is already imported
		if (mTypeImportsSingle.containsKey(simpleName)) {
			// Check if previous import is the same as new import
			if (!mTypeImportsSingle.get(simpleName).equals(canonicalName)) {
				//throw new RedeclarationParseException("Duplicate type import for " + simpleName + ". Previous declaration: " + mTypeImportsSingle.get(simpleName), this, sourceOffset);
				// TODO:
				throw new IllegalArgumentException("???");
			} else {
				return;
			}
		}

		mTypeImportsSingle.put(simpleName, canonicalName);
	}

	void addOnDemandTypeImport(String packageName) {
		mTypeImportsOnDemand.add(packageName);
	}

	void addSingleStaticImport(String staticMember, String canonicalName) {
		mStaticImportsSingle.put(staticMember, canonicalName);
	}

	void addOnDemandStaticImport(String canonicalName) {
		mStaticImportsOnDemand.add(canonicalName);
	}

	public String getPackage() {
		return mPackageName;
	}

	void setPackage(String packageName) {
		mPackageName = packageName;
	}

	String resolveFullName(String type, List<String> enclosingClasses) {
		// TODO: Correct resolution
		return null;
	}
}
