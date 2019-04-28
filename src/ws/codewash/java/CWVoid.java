package ws.codewash.java;

public enum CWVoid implements CWType {
	VOID;

	public static CWVoid get(String keyword) {
		return keyword.equals("void") ? VOID : null;
	}

	@Override
	public String getSimpleName() {
		return "void";
	}

	@Override
	public String toString() {
		return "void";
	}
}
