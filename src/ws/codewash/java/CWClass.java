package ws.codewash.java;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CWClass extends CWClassOrInterface {
	private CWClass mSuperClass;
	private Set<CWClass> mSubClasses = new HashSet<>();

	public CWClass(TypeResolver resolver, String _package, int modifiers, String name, String superClass, CWClassOrInterface outerClass, Collection<String> interfaces) {
		super(resolver, _package, modifiers, name, outerClass, interfaces);

		resolver.resolve(new PendingType<>(superClass, this::setSuperClass));
	}

	CWClass(TypeResolver resolver, Class _class) {
		super(resolver, _class);
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
