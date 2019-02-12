package ws.codewash.java;

public class CWEnum extends CWAbstractClass {
    public CWEnum(CWClassContainer parent, CWAccessModifier accessModifier, String name) {
        super(parent, accessModifier, true, name);
    }

    @Override
    public String toString() {
        return "enum " + mName + ":\n" +
                getContainer() + "\n" +
                mAccessModifier + " " + (mFinal ? "FINAL " : "") + "\n";
    }
}
