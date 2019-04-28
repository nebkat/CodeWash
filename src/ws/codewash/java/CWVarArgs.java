package ws.codewash.java;

public class CWVarArgs extends CWArray {
	CWVarArgs(CWType type) {
		super(type);
	}

	@Override
	public String getSimpleName() {
		return mType.getSimpleName() + "[]";
	}

	@Override
	public String getName() {
		return mType.getName() + "[]";
	}

	@Override
	public String getCanonicalName() {
		return mType.getCanonicalName() + "[]";
	}

	@Override
	public String toString() {
		return mType.toString() + "[]";
	}
}
