package ws.codewash.java;

import java.util.HashSet;
import java.util.Set;

public class CWPackage implements CWClassContainer {

    private final String mName;

    private CWPackage mContainer;
    private final Set<CWPackage> mPackages;
    private final Set<CWClass> mClasses;

    public CWPackage(String name){
        mName = name;
        mClasses = new HashSet<>();
        mPackages = new HashSet<>();
    }

    public void addPackage(CWPackage cwPackage) {
        this.mPackages.add(cwPackage);
    }

    public void addClass(CWClass cwClass) {
        this.mClasses.add(cwClass);
    }

    public void setContainer(CWPackage container) {
        this.mContainer = container;
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
