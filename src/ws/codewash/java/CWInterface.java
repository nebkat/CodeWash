package ws.codewash.java;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CWInterface extends CWClassOrInterface {
	private Set<CWInterface> mSubInterfaces = new HashSet<>();
	private Set<CWClassOrInterface> mImplementingClasses = new HashSet<>();

	public CWInterface(String _package, int modifiers, String name, List<String> outerClasses, Collection<String> interfaces) {
		super(_package, modifiers, name, outerClasses, interfaces);
	}

	CWInterface(Class _class) {
		super(_class);
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
