package ws.codewash.java;

import ws.codewash.java.statement.CWBlock;

public class CWInitializer extends Scope implements Locatable {
	private Location mLocation;

	private final CWClassOrInterface mParent;
	private boolean mStatic;

	private CWBlock mBlock;

	public CWInitializer(Location location, CWClassOrInterface parent, boolean _static) {
		mLocation = location;

		mParent = parent;
		mStatic = _static;
	}

	@Override
	public Location getLocation() {
		return mLocation;
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
