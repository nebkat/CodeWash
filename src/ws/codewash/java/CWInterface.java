package ws.codewash.java;

import java.util.List;

public class CWInterface extends CWAbstractClass {
    private List<CWInterface> mSupers;

    public CWInterface(CWClassContainer container, CWAccessModifier accessModifier, boolean _final, String name) {
        super(container, accessModifier, _final, name);
    }

    public List<CWInterface> getSupers() {
        return mSupers;
    }

    public void setSupers(List<CWInterface> supers) {
        mSupers = supers;
    }
}
