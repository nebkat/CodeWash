package ws.codewash.java;

public enum CWPrimitive implements CWType {
	BOOLEAN("boolean"),
	BYTE("byte"),
	CHARACTER("char"),
	DOUBLE("double"),
	FLOAT("float"),
	INT("int"),
	LONG("long"),
	SHORT("short");

	private final String mName;

	CWPrimitive(String name) {
		mName = name;
	}

	public static CWPrimitive get(String keyword) {
		for (CWPrimitive primitive : CWPrimitive.values()) {
			if (primitive.getSimpleName().equals(keyword)) {
				return primitive;
			}
		}
		return null;
	}

	@Override
	public String getSimpleName() {
		return mName;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + mName + ")";
	}
}
