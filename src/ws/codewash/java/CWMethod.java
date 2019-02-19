package ws.codewash.java;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CWMethod extends Scope implements CWConstructorOrMethod, CWParameterizable, CWMember, Modifiable {
	private final CWClassOrInterface mParent;
	private final String mName;
	private final int mModifiers;

	private CWType mReturnType;
	private String mPendingReturnType;

	private final List<CWTypeParameter> mTypeParameters = new ArrayList<>();
	private final List<CWVariable> mParameters = new ArrayList<>();

	public CWMethod(CWClassOrInterface parent, int modifiers, String name, String returnType) {
		mParent = parent;
		mName = name;

		mModifiers = modifiers;

		mPendingReturnType = returnType;
		resolve(new PendingType<>(returnType, this::setReturnType));
	}

	public void addTypeParameter(CWTypeParameter typeParameter) {
		mTypeParameters.add(typeParameter);
		addTypeDeclaration(typeParameter.getVariableName(), typeParameter);
	}

	public void addParameter(CWVariable parameter) {
		mParameters.add(parameter);
		addLocalVariableDeclaration(parameter.getName(), parameter);
	}

	public CWType getReturnType() {
		return mReturnType;
	}

	public void setReturnType(CWType type) {
		mReturnType = type;
	}

	public List<CWVariable> getParameters() {
		return mParameters;
	}

	public int getMethodLength() {
		return 0;
	}

	@Override
	public CWClassOrInterface getParent() {
		return mParent;
	}

	@Override
	public String getName() {
		return mName;
	}

	@Override
	public int getModifiers() {
		return mModifiers;
	}

	@Override
	public String toString() {
		return getModifiersForToString() + mPendingReturnType + " " + mName +
				"(" + mParameters.stream().map(CWVariable::toString).collect(Collectors.joining(", ")) + ")";
	}
}
