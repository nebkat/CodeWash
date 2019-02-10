package ws.codewash.java;

import java.util.HashSet;
import java.util.Set;

public class CWPackage implements CWClassContainer {
    private CWPackage mContainer;
	private final String mName;
    private final Set<CWPackage> mSubPackages = new HashSet<>();
    private final Set<CWClass> mClasses = new HashSet<>();

    public CWPackage(String name){
        mName = name;
    }

    public void addSubPackage(CWPackage cwPackage) {
        mSubPackages.add(cwPackage);
    }

    public Set<CWPackage> getSubPackages() {
    	return mSubPackages;
	}

    public void addClass(CWClass _class) {
        mClasses.add(_class);
    }

    public Set<CWClass> getClasses() {
    	return mClasses;
	}

	public String getName() {
    	return mName;
	}

    @Override
    public CWClassContainer getContainer() {
        return mContainer;
    }

	public void setContainer(CWPackage container) {
		mContainer = container;
	}

    @Override
    public String toString() {
        return "Package: " + mName;
    }
}
