package ws.codewash.java;

public class CWExternalClass implements Extendable {
	private final Class mClass;
	private final String mName;

	public CWExternalClass(Class _class, String name) {
		mClass = _class;
		mName = name;
	}

	@Override
	public Class getSuper() {
		return mClass.getSuperclass();
	}

	@Override
	public String getName() {
		return mName;
	}
}
