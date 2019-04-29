package ws.codewash.java;

public class CWVariable implements Modifiable, Locatable {
	private Location mLocation;

	private final String mName;
	private CWType mType;
	private RawType mPendingType;

	private final int mModifiers;

	public CWVariable(Location location, Scope enclosingScope, int modifiers, RawType type, String name) {
		mLocation = location;

		mName = name;
		mModifiers = modifiers;

		mPendingType = type;
		enclosingScope.resolve(new PendingType<>(type, this::setType));
	}

	@Override
	public Location getLocation() {
		return mLocation;
	}

	public String getName() {
		return mName;
	}

	public CWType getType() {
		return mType;
	}

	private void setType(CWType type) {
		mType = type;
	}

	@Override
	public int getModifiers() {
		return mModifiers;
	}

	@Override
	public String toString() {
		return getModifiersForToString() + mPendingType + " " + mName;
	}
}