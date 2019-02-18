package ws.codewash.java;

public class CWArray extends CWReferenceType {
	private CWType mType;

	public CWArray(CWType type) {
		mType = type;
	}

	private CWType getType() {
		return mType;
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
		return getClass().getSimpleName() + "(" + mType + ")";
	}
}