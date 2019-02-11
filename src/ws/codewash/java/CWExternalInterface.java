package ws.codewash.java;

import java.util.Arrays;
import java.util.List;

public class CWExternalInterface implements Implementable {
	private final Class mClass;
	private final String mName;

	public CWExternalInterface(Class _class, String name) {
		mClass = _class;
		mName = name;
	}

	@Override
	public List<Object> getInterfaces() {
		return Arrays.asList(mClass.getInterfaces());
	}

	@Override
	public String getName() {
		return mName;
	}

	@Override
	public String toString() {
		return "extInterface " + mName + ":\n" +
				mClass.getPackageName() + "\n";
	}
}
