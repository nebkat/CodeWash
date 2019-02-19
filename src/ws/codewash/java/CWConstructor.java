package ws.codewash.java;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CWConstructor extends Scope implements CWConstructorOrMethod, CWParameterizable, Modifiable {
	private final CWClassOrInterface mParent;

	private final int mModifiers;

	private final List<CWTypeParameter> mTypeParameters = new ArrayList<>();
	private final List<CWVariable> mParameters = new ArrayList<>();

	public CWConstructor(CWClassOrInterface parent, int modifiers) {
		mParent = parent;
		mModifiers = modifiers;
	}

	public void addTypeParameter(CWTypeParameter typeParameter) {
		mTypeParameters.add(typeParameter);
		addTypeDeclaration(typeParameter.getVariableName(), typeParameter);
	}

	public void addParameter(CWVariable parameter) {
		mParameters.add(parameter);
		addLocalVariableDeclaration(parameter.getName(), parameter);
	}

	private CWClassOrInterface getParent() {
		return mParent;
	}

	private List<CWTypeParameter> getTypeParameters() {
		return mTypeParameters;
	}

	private List<CWVariable> getParameters() {
		return mParameters;
	}

	@Override
	public int getModifiers() {
		return mModifiers;
	}

	@Override
	public String toString() {
		return getModifiersForToString() + mParent.getSimpleName() +
				"(" + mParameters.stream().map(CWVariable::toString).collect(Collectors.joining(", ")) + ")";
	}
}
