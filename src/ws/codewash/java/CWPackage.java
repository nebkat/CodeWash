package ws.codewash.java;

import static ws.codewash.java.ParsedSourceTree.dot;

public class CWPackage extends Scope {
	private String mName;

	public CWPackage(Scope enclosingScope, String name) {
		super(enclosingScope);
		mName = name;
	}

	@Override
	CWType resolveUpwards(RawType.Identifier identifier, Scope startScope) {
		String className = identifier.getIdentifier();

		if (getTypeDeclaration(className) == null) {
			getRoot().getOrInitClass(dot(mName) + className);
		}

		return super.resolveUpwards(identifier, startScope);
	}

	@Override
	public CWPackage getPackage() {
		return this;
	}

	public String getName() {
		return mName;
	}

	@Override
	public String toString() {
		return "package " + mName;
	}
}
