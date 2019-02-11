package ws.codewash.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CWClass extends CWAbstractClass implements Extendable, Implementable {
    private Extendable mSuper = null;
	private List<Implementable> mInterfaces = new ArrayList<>();

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

	public void addInterface(Implementable implementable) {
		mInterfaces.add(implementable);
	}

	@Override
	public List<Object> getInterfaces() {
		return Collections.singletonList(mInterfaces);
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