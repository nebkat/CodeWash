package ws.codewash.java;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CWClass extends CWClassOrInterface {
	private CWClass mSuperClass;
	private Set<CWClass> mSubClasses = new HashSet<>();

	public CWClass(String _package, int modifiers, String name, String superClass, List<String> outerClasses, Collection<String> interfaces) {
		super(_package, modifiers, name, outerClasses, interfaces);

		mPendingTypes.add(new PendingType<>(superClass, this::setSuperClass));
	}

	CWClass(Class _class) {
		super(_class);
	}

	@Override
	protected int getValidModifiers() {
		return Modifier.classModifiers();
	}

	private void setSuperClass(CWClass superClass) {
		mSuperClass = superClass;
		superClass.addSubClass(this);
	}

	public CWClass getSuperClass() {
		return mSuperClass;
	}

	private void addSubClass(CWClass subClass) {
		mSubClasses.add(subClass);
	}

	public Set<CWClass> getSubClasses() {
		return mSubClasses;
	}
}
