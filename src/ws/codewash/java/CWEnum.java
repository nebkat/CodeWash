package ws.codewash.java;

import java.lang.reflect.Modifier;
import java.util.Collection;

public class CWEnum extends CWClassOrInterface {
	public CWEnum(TypeResolver resolver, String _package, int modifiers, String name, CWClassOrInterface outerClass, Collection<String> interfaces) {
		super(resolver, _package, modifiers, name, outerClass, interfaces);
	}

	CWEnum(TypeResolver resolver, Class _class) {
		super(resolver, _class);
	}

	@Override
	protected int getValidModifiers() {
		return Modifier.classModifiers();
	}
}
