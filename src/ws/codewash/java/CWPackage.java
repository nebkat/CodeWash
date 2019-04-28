package ws.codewash.java;

import ws.codewash.util.Log;

import static ws.codewash.java.ParsedSourceTree.dot;

public class CWPackage extends Scope {
	private static final String TAG = CWPackage.class.getSimpleName();

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

	public String getName() {
		return mName;
	}

	@Override
	public String toString() {
		return "package " + mName;
	}
}
