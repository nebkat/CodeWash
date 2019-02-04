package ws.codewash.java;

public class CWField extends CWMember {

    private final CWType mType;

    public CWField(CWClass parent, CWAccessModifier accessModifier, boolean _final, String name, CWType type) {
        super(parent, accessModifier, _final, name);
        mType = type;
    }

}
