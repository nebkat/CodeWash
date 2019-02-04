package ws.codewash.java;

import java.util.Set;

public class CWPackage implements CWClassContainer {

    private final String mName;

    private CWPackage mContainer;
    private final Set<CWClass> mClasses;

    public CWPackage(String name, Set<CWClass> classes){
        mName = name;
        mClasses = classes;
    }

    @Override
    public CWClassContainer getContainer() {
        return mContainer;
    }
}
