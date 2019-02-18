package ws.codewash.java;

public class CWField extends CWMember {
	private CWType mType;

	public CWField(TypeResolver resolver, CWClass parent, int modifiers, String name, String type) {
		super(resolver, parent, modifiers, name);

		resolver.resolve(new PendingType<>(type, this::setType));
	}

	public CWType getType() {
		return mType;
	}

	private void setType(CWType type) {
		mType = type;
	}
}
