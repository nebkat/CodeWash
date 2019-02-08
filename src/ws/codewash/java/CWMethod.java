package ws.codewash.java;

import java.util.List;

public class CWMethod extends CWMember {
    private final CWType mReturnType;
    private final boolean mAbstract;
    private final List<CWVariable> mParameters;

    public CWMethod(CWClass parent, CWAccessModifier accessModifier, boolean _final, boolean _abstract, String name, CWType returnType, List<CWVariable> parameters) {
        super(parent, accessModifier, _final, name);
        mAbstract = _abstract;
        mReturnType = returnType;
        mParameters = parameters;
    }

    public CWType getReturnType() {
    	return mReturnType;
	}

	public boolean isAbstract() {
    	return mAbstract;
	}

	public List<CWVariable> getParameters() {
    	return mParameters;
	}
}
