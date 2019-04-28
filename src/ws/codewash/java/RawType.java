package ws.codewash.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RawType {
	public static final RawType OBJECT_RAW_TYPE = new RawType(
			Arrays.stream(Object.class.getName().split("\\."))
					.map(Identifier::new)
					.collect(Collectors.toList()));

	public static final RawType VOID_RAW_TYPE = new RawType(
			Collections.singletonList(new Identifier("void")));

	public static final RawType VAR_RAW_TYPE = new RawType(
			Collections.singletonList(new Identifier("var")));

	public static final RawType WILDCARD_RAW_TYPE = new RawType(
			Collections.singletonList(new Identifier("?")));

	private List<Identifier> mIdentifiers;
	private int mArrayDimension;
	private boolean mVarArgs;

	public RawType(List<Identifier> identifiers) {
		this(identifiers, 0);
	}

	public RawType(List<Identifier> identifiers, int arrayDimension) {
		this(identifiers, arrayDimension, false);
	}

	public RawType(List<Identifier> identifiers, int arrayDimension, boolean varArgs) {
		mIdentifiers = identifiers;
		mArrayDimension = arrayDimension;
		mVarArgs = varArgs;
	}

	public List<Identifier> getIdentifiers() {
		return mIdentifiers;
	}

	public Identifier getFirst() {
		return mIdentifiers.get(0);
	}

	public Identifier getLast() {
		return mIdentifiers.get(mIdentifiers.size() - 1);
	}

	public RawType getRemainder() {
		return new RawType(new ArrayList<>(mIdentifiers) {{
			remove(0);
		}}, mArrayDimension, mVarArgs);
	}

	public boolean isSimpleName() {
		return mIdentifiers.size() == 1;
	}

	public int getArrayDimension() {
		return mArrayDimension;
	}

	public boolean isVarArgs() {
		return mVarArgs;
	}

	// TODO: Set in constructor only
	public void setVarArgs() {
		mVarArgs = true;
	}

	public RawType append(Identifier identifier) {
		if (mArrayDimension > 0 || mVarArgs) {
			// TODO:
			throw new IllegalStateException("Appending identifier to array type");
		}

		return new RawType(new ArrayList<>(mIdentifiers) {{
			add(identifier);
		}});
	}

	@Override
	public String toString() {
		return mIdentifiers.stream()
						.map(Identifier::toString)
						.collect(Collectors.joining("."))
				+ "[]".repeat(mArrayDimension)
				+ (mVarArgs ? "..." : "");
	}

	public static class Identifier {
		private String mIdentifier;
		private List<RawType> mTypeParameters;

		public Identifier(String identifier) {
			this(identifier, Collections.emptyList());
		}

		public Identifier(String identifier, List<RawType> typeParameters) {
			mIdentifier = identifier;
			mTypeParameters = typeParameters;
		}

		public String getIdentifier() {
			return mIdentifier;
		}

		public List<RawType> getTypeParameters() {
			return mTypeParameters;
		}

		@Override
		public String toString() {
			return mIdentifier + (mTypeParameters.isEmpty() ? "" : "<" +
					mTypeParameters.stream()
							.map(RawType::toString)
							.collect(Collectors.joining(", "))
					+ ">");
		}
	}
}
