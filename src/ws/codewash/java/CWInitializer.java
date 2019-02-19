package ws.codewash.java;

public class CWInitializer {
	private final CWClassOrInterface mParent;
	private boolean mStatic;

	private CWBlock mBlock;

	public CWInitializer(CWClassOrInterface parent, boolean _static) {
		mParent = parent;
		mStatic = _static;
	}

	public boolean isStatic() {
		return mStatic;
	}
}
