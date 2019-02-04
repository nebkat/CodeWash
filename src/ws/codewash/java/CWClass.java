package ws.codewash.java;

public class CWClass extends CWAbstractClass {
    private CWClass mSuper;

    private final boolean mAbstract;

    public CWClass(CWClassContainer container, CWAccessModifier accessModifier, boolean _final, boolean _abstract, String name) {
        super(container, accessModifier, _final, name);
        mAbstract = _abstract;
    }

    public CWClass getSuper() {
        return mSuper;
    }

    public void setSuper(CWClass _super) {
        mSuper = _super;
    }
}