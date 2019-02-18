package ws.codewash.java;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static ws.codewash.parser.ParsedSourceTree.dot;

public abstract class CWClassOrInterface extends CWReferenceType implements Modifiable {
	private final Class mClass;
	private final String mPackageName;
	private Set<CWInterface> mInterfaces = new HashSet<>();
	private final String mName;

	private final int mModifiers;

	private CWClassOrInterface mOuterClass;
	private Set<CWClassOrInterface> mInnerClasses = new HashSet<>();

	CWClassOrInterface(TypeResolver resolver, String packageName, int modifiers, String name, CWClassOrInterface outerClass, Collection<String> interfaces) {
		mClass = null;
		mPackageName = packageName;
		mName = name;

		mModifiers = modifiers;

		// Outer class
		if (outerClass != null) {
			setOuterClass(outerClass);
		}

		// Interfaces
		for (String _interface : interfaces) {
			resolver.resolve(new PendingType<>(_interface, this::addInterface));
		}
	}

	CWClassOrInterface(TypeResolver resolver, Class _class) {
		mClass = _class;
		mPackageName = _class.getPackageName();
		mName = _class.getSimpleName();

		mModifiers = _class.getModifiers();

		// Outer class
		if (_class.getEnclosingClass() != null) {
			resolver.resolve(new PendingType<>(_class.getEnclosingClass().getName(), this::setOuterClass));
		}

		// Interfaces
		for (Class _interface : _class.getInterfaces()) {
			resolver.resolve(new PendingType<>(_interface.getName(), this::addInterface));
		}
	}

	public static CWClassOrInterface forExternalClass(TypeResolver resolver, Class _class) {
		CWClassOrInterface cwClassOrInterface;
		if (_class.isEnum()) {
			cwClassOrInterface = new CWEnum(resolver, _class);
		} else if (_class.isInterface()) {
			cwClassOrInterface = new CWInterface(resolver, _class);
		} else {
			cwClassOrInterface = new CWClass(resolver, _class);
		}

		return cwClassOrInterface;
	}

	protected abstract int getValidModifiers();

	public void setOuterClass(CWClassOrInterface outerClass) {
		mOuterClass = outerClass;
		outerClass.addInnerClass(this);
	}

	public CWClassOrInterface getOuterClass() {
		return mOuterClass;
	}

	public void addInnerClass(CWClassOrInterface innerClass) {
		mInnerClasses.add(innerClass);
	}

	public Set<CWClassOrInterface> getInnerClasses() {
		return mInnerClasses;
	}

	protected void addInterface(CWInterface _interface) {
		_interface.addImplementingClass(this);
		mInterfaces.add(_interface);
	}

	public Set<CWInterface> getInterfaces() {
		return mInterfaces;
	}

	public String getSimpleName() {
		if (mClass != null) return mClass.getSimpleName();
		return mName;
	}

	public String getName() {
		if (mClass != null) return mClass.getName();
		return getHierarchicalName("$");
	}

	public String getCanonicalName() {
		if (mClass != null) return mClass.getCanonicalName();
		return getHierarchicalName(".");
	}

	public String getHierarchicalName(String classDelimiter) {
		if (mOuterClass == null) {
			return dot(mPackageName) + mName;
		}
		return mOuterClass.getHierarchicalName(classDelimiter) + classDelimiter + mName;
	}

	public String getPackageName() {
		return mPackageName;
	}

	public boolean isExternal() {
		return mClass != null;
	}

	public Class getJavaClass() {
		return mClass;
	}

	public boolean isInner() {
		return mOuterClass != null;
	}

	public int getModifiers() {
		return mModifiers;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + getCanonicalName() + ")";
	}
}
