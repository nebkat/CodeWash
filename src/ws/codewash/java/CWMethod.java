package ws.codewash.java;


import ws.codewash.java.statement.CWBlock;
import ws.codewash.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CWMethod extends Scope implements CWConstructorOrMethod, CWParameterizable, CWMember, Modifiable {
	private final CWClassOrInterface mParent;
	private final String mName;
	private final int mModifiers;

	private CWType mReturnType;

	private final List<CWTypeParameter> mTypeParameters = new ArrayList<>();
	private final List<CWVariable> mParameters = new ArrayList<>();

	private CWBlock mBlock;

	public CWMethod(CWClassOrInterface parent, int modifiers, String name, RawType returnType) {
		super(parent);

		mParent = parent;
		mName = name;

		mModifiers = modifiers;

		resolve(new PendingType<>(returnType, this::setReturnType));
	}

	public void addTypeParameter(CWTypeParameter typeParameter) {
		mTypeParameters.add(typeParameter);
		addTypeDeclaration(typeParameter);
	}

	public void addParameter(CWVariable parameter) {
		mParameters.add(parameter);
		addLocalVariableDeclaration(parameter);
	}

	public CWType getReturnType() {
		return mReturnType;
	}

	public void setReturnType(CWType type) {
		mReturnType = type;
	}

	public CWBlock getBlock() {
		return mBlock;
	}

	public void setBlock(CWBlock block) {
		mBlock = block;
	}

	public List<CWVariable> getParameters() {
		return mParameters;
	}

	public int getMethodLength() {

		if (mBlock != null) {
			String tmpContent = mBlock.getContent();
			int blankLines = 0;

			String[] lines = tmpContent.split("\n");

			for (String line : lines) {
				line = line.trim();
				if (line.matches("\\w+") || line.isEmpty()) {
					blankLines++;
				}
			}
			return lines.length - blankLines- 2;
		}
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
		StringBuilder builder = new StringBuilder();

		builder.append(getModifiersForToString());
		if (!mTypeParameters.isEmpty()) {
			builder.append("<");
			builder.append(mTypeParameters.stream()
					.map(CWTypeParameter::toString)
					.collect(Collectors.joining(", ")));
			builder.append(">");
		}
		builder.append(getName());
		builder.append("(");
		builder.append(mParameters.stream()
				.map(CWVariable::toString)
				.collect(Collectors.joining(", ")));
		builder.append(")");
		builder.append(" { }");

		return builder.toString();
	}
}
