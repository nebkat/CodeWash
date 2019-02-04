package ws.codewash.java;

import java.util.List;

public class CWConstructor {
    private final CWClass mClass;
    private final List<CWVariable> mParameters;

    public CWConstructor(CWClass parent, List<CWVariable> parameters) {
        mClass = parent;
        mParameters = parameters;
    }
}
