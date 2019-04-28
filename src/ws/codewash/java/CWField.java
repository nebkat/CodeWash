package ws.codewash.java;

public class CWField extends CWVariable implements CWMember, Modifiable {
	private CWClassOrInterface mParent;

	public CWField(CWClassOrInterface parent, int modifiers, RawType type, String name) {
		super(parent, modifiers, type, name);

		mParent = parent;
	}

	@Override
	public CWClassOrInterface getParent() {
		return mParent;
	}
}
