package ws.codewash.java;

import ws.codewash.parser.ParsedSourceTree;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import static ws.codewash.parser.ParsedSourceTree.dot;

public abstract class CWClassOrInterface extends CWReferenceType implements PendingTypeReceiver {
	private final Class mClass;
	private final String mPackageName;
	private Set<CWInterface> mInterfaces = new HashSet<>();
	private final String mName;

	private final int mModifiers;

	private CWClassOrInterface mOuterClass;
	private String[] mOuterClasses = new String[0];
	private Set<CWClassOrInterface> mInnerClasses = new HashSet<>();

	Set<PendingType> mPendingTypes = new HashSet<>();

	CWClassOrInterface(String packageName, int modifiers, String name, List<String> outerClasses, Collection<String> interfaces) {
		mClass = null;
		mPackageName = packageName;
		mName = name;

		mModifiers = modifiers;

		// Outer class
		if (outerClasses.size() > 0) {
			mOuterClasses = new String[outerClasses.size()];
			outerClasses.toArray(mOuterClasses);

			String outerClass = dot(packageName) + String.join("$", outerClasses);
			mPendingTypes.add(new PendingType<>(outerClass, this::setOuterClass));
		}

		// Interfaces
		for (String _interface : interfaces) {
			mPendingTypes.add(new PendingType<>(_interface, this::addInterface));
		}
	}

	CWClassOrInterface(Class _class) {
		mClass = _class;
		mPackageName = _class.getPackageName();
		mName = _class.getSimpleName();

		mModifiers = _class.getModifiers();

		// Outer class
		if (_class.getEnclosingClass() != null) {
			List<String> outerClasses = new ArrayList<>();

			Class enclosingClass = _class;
			while ((enclosingClass = enclosingClass.getEnclosingClass()) != null) {
				outerClasses.add(0, enclosingClass.getSimpleName());
			}

			mOuterClasses = new String[outerClasses.size()];
			outerClasses.toArray(mOuterClasses);

			String outerClass = dot(mPackageName) + String.join("$", outerClasses);
			mPendingTypes.add(new PendingType<>(outerClass, this::setOuterClass));
		}

		// Interfaces
		for (Class _interface : _class.getInterfaces()) {
			mPendingTypes.add(new PendingType<>(_interface.getName(), this::addInterface));
		}
	}

	public static CWClassOrInterface forExternalClass(Class _class) {
		CWClassOrInterface cwClassOrInterface;
		if (_class.isEnum()) {
			cwClassOrInterface = new CWEnum(_class);
		} else if (_class.isInterface()) {
			cwClassOrInterface = new CWInterface(_class);
		} else {
			cwClassOrInterface = new CWClass(_class);
		}

		return cwClassOrInterface;
	}

	protected abstract int getValidModifiers();

	@Override
	public Set<PendingType> getPendingTypes() {
		return mPendingTypes;
	}

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
		return mName;
	}

	public String getName() {
		return getHierarchicalName("$");
	}

	public String getCanonicalName() {
		return getHierarchicalName(".");
	}

	public String getHierarchicalName(String classDelimiter) {
		String outerClasses = String.join(classDelimiter, mOuterClasses);

		return dot(mPackageName) + (outerClasses.isEmpty() ? "" : outerClasses + classDelimiter) + mName;
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

	private CWAccessModifier getAccess() {
		if (Modifier.isPublic(mModifiers)) {
			return CWAccessModifier.PUBLIC;
		} else if (Modifier.isProtected(mModifiers)) {
			return CWAccessModifier.PROTECTED;
		} else if (Modifier.isPrivate(mModifiers)) {
			return CWAccessModifier.PRIVATE;
		} else {
			return CWAccessModifier.PACKAGE;
		}
	}

	public boolean isInner() {
		return mOuterClass != null;
	}

	public boolean isFinal() {
		return Modifier.isFinal(mModifiers);
	}

	public boolean isAbstract() {
		return Modifier.isAbstract(mModifiers);
	}

	public boolean isStatic() {
		return Modifier.isStatic(mModifiers);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "<" + getCanonicalName() + ">" + (isExternal() ? "(external)" : "");
	}
}
