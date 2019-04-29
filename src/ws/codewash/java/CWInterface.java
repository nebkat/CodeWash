package ws.codewash.java;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CWInterface extends CWClassOrInterface {
	private Set<CWInterface> mSubInterfaces = new HashSet<>();
	private Set<CWClassOrInterface> mImplementingClasses = new HashSet<>();

	public CWInterface(Location location, Scope enclosingScope, int modifiers, String name, List<RawType> interfaces) {
		super(location, enclosingScope, modifiers, name, interfaces);
	}

	CWInterface(Scope enclosingScope, Class _class) {
		super(enclosingScope, _class);
	}

	@Override
	protected int getValidModifiers() {
		return Modifier.interfaceModifiers();
	}

	public Set<CWInterface> getSubInterfaces() {
		return mSubInterfaces;
	}

	void addImplementingClass(CWClassOrInterface _class) {
		mImplementingClasses.add(_class);
		if (_class instanceof CWInterface) {
			mSubInterfaces.add((CWInterface) _class);
		}
	}

	public Set<CWClassOrInterface> getImplementingClasses() {
		return mImplementingClasses;
	}
}
