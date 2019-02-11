package ws.codewash.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CWInterface extends CWAbstractClass implements Implementable {
    private List<Implementable> mInterfaces = new ArrayList<>();

    public CWInterface(CWClassContainer container, CWAccessModifier accessModifier, boolean _final, String name) {
        super(container, accessModifier, _final, name);
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
        return "interface " + mName + ":\n" +
                mAccessModifier + " " + (mFinal ? "FINAL " : "");
    }
}
