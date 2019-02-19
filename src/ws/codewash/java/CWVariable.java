package ws.codewash.java;

import java.lang.reflect.Modifier;

public class CWVariable implements Modifiable {
	private final String mName;
	private CWType mType;
	private String mPendingType;

	private final int mModifiers;

	public CWVariable(Scope enclosingScope, int modifiers, String type, String name) {
		mName = name;
		mModifiers = modifiers;

		mPendingType = type;

		enclosingScope.resolve(new PendingType<>(type, this::setType));
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