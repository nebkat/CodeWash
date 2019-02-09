package ws.codewash.java;

import java.util.HashMap;
import java.util.Map;

public class CWSourceTree {
    private Map<String, CWPackage> mPackages = new HashMap<>();

    public void setPackages(Map<String, CWPackage> packages) {
        mPackages = packages;
    }

    public Map<String, CWPackage> getPackages() {
        return mPackages;
    }
}
