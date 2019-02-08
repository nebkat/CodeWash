package ws.codewash.java;

public class CWArray implements CWType {
    private final CWType mType;

    public CWArray(CWType type) {
        mType = type;
    }

    private CWType getType() {
    	return mType;
	}
}
