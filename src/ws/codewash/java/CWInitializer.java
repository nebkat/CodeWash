package ws.codewash.java;

import ws.codewash.java.statement.CWBlock;

public class CWInitializer extends Scope {
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

	public CWBlock getBlock() {
		return mBlock;
	}

	public void setBlock(CWBlock block) {
		mBlock = block;
	}
}
