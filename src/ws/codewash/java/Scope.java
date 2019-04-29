package ws.codewash.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Scope {
	protected Scope mEnclosingScope;
	private List<Scope> mSuperScopes = new ArrayList<>();

	private Map<String, CWType> mTypeDeclarations = Collections.synchronizedMap(new HashMap<>());
	private Map<String, CWField> mFieldDeclarations = new HashMap<>();
	private Map<String, CWMethod> mMethodDeclarations = new HashMap<>();
	private Map<String, CWVariable> mLocalVariableDeclarations = new HashMap<>();

	private Set<PendingType> mPendingTypes = new HashSet<>();
	private List<Scope> mChildren = new ArrayList<>();

	public Scope() {
	}

	public Scope(Scope enclosingScope) {
		mEnclosingScope = enclosingScope;
		mEnclosingScope.addChild(this);
	}

	private void addChild(Scope child) {
		mChildren.add(child);
	}

	protected void addSuperScope(Scope superScope) {
		mSuperScopes.add(superScope);
	}

	public void addTypeDeclaration(CWType type) {
		mTypeDeclarations.put(type.getSimpleName(), type);
	}

	public CWType getTypeDeclaration(String simpleName) {
		return mTypeDeclarations.get(simpleName);
	}

	public void addFieldDeclaration(String simpleName, CWField field) {
		mFieldDeclarations.put(simpleName, field);
	}

	public void addMethodDeclaration(String simpleName, CWMethod method) {
		mMethodDeclarations.put(simpleName, method);
	}

	public void addLocalVariableDeclaration(CWVariable variable) {
		mLocalVariableDeclarations.put(variable.getName(), variable);
	}

	public Scope getEnclosingScope() {
		return mEnclosingScope;
	}

	public void resolvePendingTypes() {
		// Loop until all pending types have been resolved
		while (mPendingTypes.size() > 0) {
			// Prevent concurrent modification exception by copying locally
			Set<PendingType> pendingTypes = new HashSet<>(mPendingTypes);
			mPendingTypes.clear();

			for (PendingType pendingType : pendingTypes) {
				RawType rawType = pendingType.getRawType();
				CWType type = resolveStart(rawType);

				if (type == null) {
					throw new IllegalStateException("Could not resolve type " + rawType + " in " + this);
				}

				pendingType.accept(type);
			}
		}

		for (Scope child : new ArrayList<>(mChildren)) {
			child.resolvePendingTypes();
		}
	}

	public void resolve(PendingType pendingType) {
		mPendingTypes.add(pendingType);
	}

	private CWType resolveStart(RawType type) {
		RawType.Identifier currentIdentifier = type.getFirst();

		// Attempt to resolve as primitive or void
		CWType resolvedType = CWPrimitive.get(currentIdentifier.getIdentifier());
		if (resolvedType == null) resolvedType = CWVoid.get(currentIdentifier.getIdentifier());
		if (resolvedType == null) resolvedType = CWWildcard.get(currentIdentifier.getIdentifier());

		// Primitive or void matched
		if (resolvedType != null) {
			// Can't have inner type of primitive type
			if (!type.isSimpleName()) {
				// TODO
				throw new IllegalStateException("Attempting to access inner class of primitive type");
			}
		} else {
			// Qualified type
			resolvedType = resolveQualified(type, this);
			if (resolvedType == null) resolvedType = mEnclosingScope.resolveOutwards(type, this);
		}

		// Array dimension
		if (type.getArrayDimension() > 0) {
			for (int i = 0; i < type.getArrayDimension(); i++) {
				resolvedType = new CWArray(resolvedType);
			}
		}

		// Variable arguments
		if (type.isVarArgs()) {
			resolvedType = new CWVarArgs(resolvedType);
		}

		return resolvedType;
	}

	public CWType resolveQualified(RawType type, Scope startScope) {
		CWType resolvedType = resolveUpwards(type.getFirst(), startScope);
		if (resolvedType == null) return null;

		if (type.isSimpleName()) {
			return resolvedType;
		}

		if (!(resolvedType instanceof Scope)) {
			// TODO:
			throw new IllegalStateException("Attempting to access type defined in non-scope");
		}

		return ((Scope) resolvedType).resolveQualified(type.getRemainder(), startScope);
	}

	protected CWType resolveOutwards(RawType type, Scope startScope) {
		CWType resolvedType = resolveQualified(type, startScope);
		if (resolvedType != null) {
			return resolvedType;
		}

		return mEnclosingScope.resolveOutwards(type, startScope);
	}

	CWType resolveUpwards(RawType.Identifier identifier, Scope startScope) {
		// Check own type declarations
		CWType resolvedType = getTypeDeclaration(identifier.getIdentifier());

		// Check super scope type declarations, but throw error on ambiguity
		if (resolvedType == null) resolvedType = mSuperScopes.stream()
				.map(superScope -> superScope.resolveUpwards(identifier, startScope))
				.filter(Objects::nonNull)
				.reduce((a, b) -> {
					throw new IllegalStateException("Multiple elements: " + a + ", " + b);
				})
				.orElse(null);

		resolvedType = resolveParameterizedType(identifier, resolvedType, startScope);

		return resolvedType;
	}

	public CWType resolveParameterizedType(RawType.Identifier identifier, CWType resolvedType, Scope startScope) {
		if (resolvedType == null || identifier.getTypeParameters().isEmpty()) return resolvedType;

		if (!(resolvedType instanceof CWClassOrInterface)) {
			// TODO:
			throw new IllegalStateException("Parameters on non parameterizable type " + resolvedType);
		}

		resolvedType = new CWParameterizedType((CWClassOrInterface) resolvedType,
				identifier.getTypeParameters().stream()
						.map(startScope::resolveStart)
						.collect(Collectors.toList()));

		return resolvedType;

	}

	public CWPackage getPackage() {
		return mEnclosingScope.getPackage();
	}

	public ParsedSourceTree getRoot() {
		return mEnclosingScope.getRoot();
	}
}
