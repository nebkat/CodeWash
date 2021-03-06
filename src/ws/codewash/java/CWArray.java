package ws.codewash.java;

public class CWArray extends CWReferenceType {
	CWType mType;

	CWArray(CWType type) {
		super();
		mType = type;
	}

	public CWType getType() {
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
		return mType.toString() + "[]";
	}
}