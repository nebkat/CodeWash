package ws.codewash.java;

import java.util.HashSet;
import java.util.Set;

public class CWPackage implements CWClassContainer {

    private final String mName;

    private CWPackage mContainer;
    private final Set<CWPackage> mPackages = new HashSet<>();
    private final Set<CWClass> mClasses = new HashSet<>();

    public CWPackage(String name){
        mName = name;
    }

    public void addPackage(CWPackage cwPackage) {
        mPackages.add(cwPackage);
    }

    public void addClass(CWClass cwClass) {
        mClasses.add(cwClass);
    }

    public void setContainer(CWPackage container) {
        mContainer = container;
    }

    @Override
    public CWClassContainer getContainer() {
        return mContainer;
    }

    @Override
    public String toString() {
        return "Package: " + mName;
    }
}
