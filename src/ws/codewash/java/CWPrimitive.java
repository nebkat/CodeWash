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

	private final String mKeyword;

	CWPrimitive(String keyword) {
		mKeyword = keyword;
	}

	public static CWPrimitive get(String keyword) {
		for (CWPrimitive primitive : CWPrimitive.values()) {
			if (primitive.getKeyword().equals(keyword)) {
				return primitive;
			}
		}
		return null;
	}

	public String getKeyword() {
		return mKeyword;
	}
}
