package ws.codewash.java;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CWInterface extends CWClassOrInterface {
	private Set<CWInterface> mSubInterfaces = new HashSet<>();
	private Set<CWClassOrInterface> mImplementingClasses = new HashSet<>();

	public CWInterface(TypeResolver resolver, String _package, int modifiers, String name, CWClassOrInterface outerClass, Collection<String> interfaces) {
		super(resolver, _package, modifiers, name, outerClass, interfaces);
	}

	CWInterface(TypeResolver resolver, Class _class) {
		super(resolver, _class);
	}

	@Override
	protected int getValidModifiers() {
		return Modifier.interfaceModifiers();
	}

	private void addSubInterface(CWInterface _interface) {
		mSubInterfaces.add(_interface);
	}

	public Set<CWInterface> getSubInterfaces() {
		return mSubInterfaces;
	}

	@Override
	protected void addInterface(CWInterface _interface) {
		super.addInterface(_interface);
		_interface.addSubInterface(this);
	}

	void addImplementingClass(CWClassOrInterface _class) {
		mImplementingClasses.add(_class);
	}

	public Set<CWClassOrInterface> getImplementingClasses() {
		return mImplementingClasses;
	}
}
