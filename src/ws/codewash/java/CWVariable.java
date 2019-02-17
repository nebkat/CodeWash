package ws.codewash.java;

public class CWVariable {
	private final CWType mType;
	private final String mName;

	public CWVariable(CWType type, String name) {
		mType = type;
		mName = name;
	}

	public CWType getType() {
		return mType;
	}

	public String getName() {
		return mName;
	}
}