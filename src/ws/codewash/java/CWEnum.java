package ws.codewash.java;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class CWEnum extends CWClassOrInterface {
	private List<String> mConstants = new ArrayList<>();

	public CWEnum(Scope enclosingScope, int modifiers, String name, List<RawType> interfaces) {
		super(enclosingScope, modifiers, name, interfaces);
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
