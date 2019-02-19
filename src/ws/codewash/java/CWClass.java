package ws.codewash.java;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CWClass extends CWClassOrInterface {
	private CWClass mSuperClass;
	private String mPendingSuperClass;
	private Set<CWClass> mSubClasses = new HashSet<>();

	public CWClass(Scope enclosingScope, String _package, int modifiers, String name, String superClass, Collection<String> interfaces) {
		super(enclosingScope, _package, modifiers, name, interfaces);

		mPendingSuperClass = superClass;
		enclosingScope.resolve(new PendingType<>(superClass, this::setSuperClass));
	}

	CWClass(Scope enclosingScope, Class _class) {
		super(enclosingScope, _class);
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append(getModifiersForToString());
		builder.append("class ");
		builder.append(getSimpleName());
		if (!mPendingSuperClass.equals(Object.class.getName())) {
			builder.append("extends ").append(mPendingSuperClass);
		}

		return builder.toString();
	}
}
