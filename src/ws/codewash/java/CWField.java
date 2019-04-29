package ws.codewash.java;

public class CWField extends CWVariable implements CWMember {
	private CWClassOrInterface mParent;

	public CWField(Location location, CWClassOrInterface parent, int modifiers, RawType type, String name) {
		super(location, parent, modifiers, type, name);

		mParent = parent;
	}

	@Override
	public CWClassOrInterface getParent() {
		return mParent;
	}
}
