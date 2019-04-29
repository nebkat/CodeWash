package ws.codewash.java;

import ws.codewash.util.Log;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CWClass extends CWClassOrInterface {
	private CWClass mSuperClass;
	private Set<CWClass> mSubClasses = new HashSet<>();

	public CWClass(Scope enclosingScope, int modifiers, String name, RawType superClass, List<RawType> interfaces) {
		super(enclosingScope, modifiers, name, interfaces);

		resolve(new PendingType<>(superClass, this::setSuperClass));
	}

	CWClass(Scope enclosingScope, Class _class) {
		super(enclosingScope, _class);
	}

	@Override
	protected int getValidModifiers() {
		return Modifier.classModifiers();
	}

	private void setSuperClass(CWType superType) {
		if (superType instanceof CWParameterizedType) {
			superType = ((CWParameterizedType) superType).getType();
		}

		if (!(superType instanceof CWClass)) {
			// TODO:
			throw new IllegalStateException("Attempting to set non-class " + superType.getName() + " as superclass of " + getName());
		}

		mSuperClass = (CWClass) superType;
		addSuperScope((CWClass) superType);
		((CWClass) superType).addSubClass(this);
	}

	public CWClass getSuperClass() {
		return mSuperClass;
	}

	public boolean hasSuperClass() {
		return !mSuperClass.getName().equals(Object.class.getName());
	}

	private void addSubClass(CWClass subClass) {
		mSubClasses.add(subClass);
	}

	public Set<CWClass> getSubClasses() {
		return mSubClasses;
	}
}
