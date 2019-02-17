package ws.codewash.java;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;

public class CWEnum extends CWClassOrInterface {
	public CWEnum(String _package, int modifiers, String name, List<String> outerClasses, Collection<String> interfaces) {
		super(_package, modifiers, name, outerClasses, interfaces);
	}

	CWEnum(Class _class) {
		super(_class);
	}

	@Override
	protected int getValidModifiers() {
		return Modifier.classModifiers();
	}
}
