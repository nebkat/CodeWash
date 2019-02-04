package ws.codewash.java;

public abstract class CWAbstractClass implements CWType, CWClassContainer {

    private final CWClassContainer mContainer;
    protected final String mName;
    protected final CWAccessModifier mAccessModifier;
    protected final boolean mFinal;

    public CWAbstractClass(CWClassContainer container, CWAccessModifier accessModifier, boolean _final, String name) {
        mContainer = container;
        mAccessModifier = accessModifier;
        mFinal = _final;
        mName = name;
    }

    @Override
    public CWClassContainer getContainer() {
        return mContainer;
    }
}
