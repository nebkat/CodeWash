package ws.codewash.java;

import java.util.List;

public class CWMethod extends CWMember {
    private CWType mReturnType;

    private final List<CWVariable> mParameters;

    public CWMethod(TypeResolver resolver, CWClass parent, int modifiers, String name, String returnType, List<CWVariable> parameters) {
        super(resolver, parent, modifiers, name);
        mParameters = parameters;

		resolver.resolve(new PendingType<>(returnType, this::setReturnType));
    }

    public CWType getReturnType() {
    	return mReturnType;
	}

	private void setReturnType(CWType returnType) {
		mReturnType = returnType;
	}

	public List<CWVariable> getParameters() {
    	return mParameters;
	}
}
