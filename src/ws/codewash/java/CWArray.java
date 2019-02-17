package ws.codewash.java;

import java.util.HashSet;
import java.util.Set;

public class CWArray extends CWReferenceType implements PendingTypeReceiver {
	private CWType mType;

	private Set<PendingType> mPendingTypes = new HashSet<>();

	public CWArray(String type) {
		// Check if primitive provided, otherwise request type
		if (CWPrimitive.get(type) != null) {
			mType = CWPrimitive.get(type);
		} else {
			mPendingTypes.add(new PendingType<>(type, this::setType));
		}
	}

	private void setType(CWReferenceType type) {
		mType = type;
	}

	private CWType getType() {
		return mType;
	}

	@Override
	public Set<PendingType> getPendingTypes() {
		return mPendingTypes;
	}
}