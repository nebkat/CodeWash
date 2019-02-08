package ws.codewash.java;

import java.util.List;

public class CWConstructor {
    private final CWClass mParent;
    private final List<CWVariable> mParameters;

    public CWConstructor(CWClass parent, List<CWVariable> parameters) {
        mParent = parent;
        mParameters = parameters;
    }

    private CWClass getParent() {
    	return mParent;
	}

	private List<CWVariable> getParameters() {
    	return mParameters;
	}
}
