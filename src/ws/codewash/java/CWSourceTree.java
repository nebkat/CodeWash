package ws.codewash.java;

import java.util.HashMap;
import java.util.Map;

public class CWSourceTree {
    private Map<String, CWPackage> mPackages = new HashMap<>();
    private Map<String, CWAbstractClass> mAbstractClasses = new HashMap<>();
    private Map<String, CWExternalClass> mExternalClasses = new HashMap<>();
    private Map<String, CWExternalInterface> mExternalInterfaces = new HashMap<>();

    public void setPackages(Map<String, CWPackage> packages) {
        mPackages = packages;
    }

    public Map<String, CWPackage> getPackages() {
        return mPackages;
    }

	public void setAbstractClasses(Map<String, CWAbstractClass> abstractClasses) {
		mAbstractClasses = abstractClasses;
	}

	public Map<String, CWAbstractClass> getAbstractClasses() {
    	return mAbstractClasses;
	}

    public void addExternalClass(String key, CWExternalClass extClass) {
        mExternalClasses.put(key, extClass);
    }

    public void addExternalInterface(String key, CWExternalInterface extInterface) {
        mExternalInterfaces.put(key, extInterface);
    }

    public Map<String, CWExternalClass> getExtClasses() {
        return mExternalClasses;
    }

    public Map<String, CWExternalInterface> getExtInterfaces() {
        return mExternalInterfaces;
    }
}
