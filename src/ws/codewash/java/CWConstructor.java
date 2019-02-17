package ws.codewash.java;

import java.util.List;

public class CWConstructor {
	private final CWClassOrInterface mParent;
	private final List<CWVariable> mParameters;

	public CWConstructor(CWClassOrInterface parent, List<CWVariable> parameters) {
		mParent = parent;
		mParameters = parameters;
	}

	private CWClassOrInterface getParent() {
		return mParent;
	}

	private List<CWVariable> getParameters() {
		return mParameters;
	}
}
