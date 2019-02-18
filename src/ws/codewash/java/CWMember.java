package ws.codewash.java;

public abstract class CWMember implements Modifiable {
	private final CWClass mParent;
	private final String mName;

	private final int mModifiers;

	CWMember(TypeResolver resolver, CWClass parent, int modifiers, String name) {
		mParent = parent;
		mModifiers = modifiers;
		mName = name;
	}

	public CWClass getParent() {
		return mParent;
	}

	public String getName() {
		return mName;
	}

	@Override
	public int getModifiers() {
		return mModifiers;
	}
}
