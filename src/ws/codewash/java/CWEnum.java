package ws.codewash.java;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CWEnum extends CWClassOrInterface {
	private List<String> mConstants = new ArrayList<>();

	public CWEnum(Scope enclosingScope, String _package, int modifiers, String name, Collection<String> interfaces) {
		super(enclosingScope, _package, modifiers, name, interfaces);
	}

	CWEnum(Scope enclosingScope, Class _class) {
		super(enclosingScope, _class);
	}

	public List<String> getConstants() {
		return mConstants;
	}

	public void addConstant(String constant) {
		mConstants.add(constant);
	}

	@Override
	protected int getValidModifiers() {
		return Modifier.classModifiers();
	}
}
