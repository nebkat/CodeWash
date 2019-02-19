package ws.codewash.java;

import java.util.HashMap;
import java.util.Map;

public class Scope implements TypeResolver {
	private Scope mEnclosingScope;

	private Map<String, CWType> mTypeDeclarations = new HashMap<>();
	private Map<String, CWField> mFieldDeclarations = new HashMap<>();
	private Map<String, CWMethod> mMethodDeclarations = new HashMap<>();
	private Map<String, CWVariable> mLocalVariableDeclarations = new HashMap<>();

	public Scope() {}

	public Scope(Scope enclosingScope) {
		mEnclosingScope = enclosingScope;
	}

	protected void addTypeDeclaration(String simpleName, CWType type) {
		mTypeDeclarations.put(simpleName, type);
	}

	protected void addFieldDeclaration(String simpleName, CWField field) {
		mFieldDeclarations.put(simpleName, field);
	}

	protected void addMethodDeclaration(String simpleName, CWMethod method) {
		mMethodDeclarations.put(simpleName, method);
	}

	protected void addLocalVariableDeclaration(String simpleName, CWVariable variable) {
		mLocalVariableDeclarations.put(simpleName, variable);
	}

	private CWType resolveType(String type) {
		if (mTypeDeclarations.containsKey(type)) {
			return mTypeDeclarations.get(type);
		}

		return mEnclosingScope.resolveType(type);
	}

	public Scope getEnclosingScope() {
		return mEnclosingScope;
	}

	@Override
	public void resolve(PendingType pendingType) {

	}

	@Override
	public CWType resolveImmediate(String typeName) {
		return null;
	}
}
