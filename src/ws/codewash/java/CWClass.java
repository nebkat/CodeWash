package ws.codewash.java;

public class CWClass extends CWAbstractClass implements Extendable {
    private Extendable mSuper = null;

    private final boolean mAbstract;
    private final boolean mStatic;

    public CWClass(CWClassContainer container, CWAccessModifier accessModifier, boolean _final, boolean _abstract, boolean _static, String name) {
        super(container, accessModifier, _final, name);
        mAbstract = _abstract;
        mStatic = _static;
    }

    public Extendable getSuper() {
        return mSuper;
    }

    public void setSuper(Extendable _super) {
        mSuper = _super;
    }

    public boolean isAbstract() {
    	return mAbstract;
	}

	@Override
	public String getName() {
		return mName;
	}

	@Override
	public String toString() {
        return "class " + mName + ":\n" +
                mAccessModifier + " " + (mStatic ? "STATIC " : "") + (mFinal ? "FINAL " : "") +
                (mAbstract ? "ABSTRACT " : "") + "\n" +
                (mSuper != null ? "EXTENDS " + mSuper.getName() + "\n" : "");
    }
}