package ws.codewash.java;


public class CWVariable {
	private CWType mType;
	private final String mName;

	public CWVariable(TypeResolver resolver, String type, String name) {
		mName = name;

		resolver.resolve(new PendingType<>(type, this::setType));
	}

	public CWType getType() {
		return mType;
	}

	public String getName() {
		return mName;
	}

	private void setType(CWType type) {
		mType = type;
	}
}