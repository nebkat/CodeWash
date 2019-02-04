package ws.codewash.java;

import java.util.List;

public abstract class CWMember {
    private final CWClass mParent;
    private final CWAccessModifier mAccessModifier;
    private final String mName;
    private final boolean mFinal;
    private List<CWAnnotation> mAnnotations;

    protected CWMember(CWClass parent, CWAccessModifier accessModifier, boolean _final, String name) {
        mParent = parent;
        mAccessModifier = accessModifier;
        mFinal = _final;
        mName = name;
    }
}
