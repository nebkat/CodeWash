package ws.codewash.java;

public class CWClass extends CWAbstractClass {
    private CWClass mSuper;

    private final boolean mAbstract;
    private final boolean mStatic;

    public CWClass(CWClassContainer container, CWAccessModifier accessModifier, boolean _final, boolean _abstract, boolean _static, String name) {
        super(container, accessModifier, _final, name);
        mAbstract = _abstract;
        mStatic = _static;
    }

    public CWClass getSuper() {
        return mSuper;
    }

    public void setSuper(CWClass _super) {
        mSuper = _super;
    }

    public boolean isAbstract() {
    	return mAbstract;
	}

	@Override
	public String toString() {
        return mName + ":\n" +
                mAccessModifier + " " + (mStatic ? "STATIC " : "") + (mFinal ? "FINAL " : "") + (mAbstract ? "ABSTRACT " : "");
    }
}