package ws.codewash.java;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CWTypeParameter implements CWType {
	private String mName;
	private Set<CWType> mBounds = new HashSet<>();

	public CWTypeParameter(CWParameterizable parent, String name, List<RawType> bounds) {
		mName = name;

		for (RawType pendingType : bounds) {
			// Type parameter bounds scope is the enclosing scope of the parameterizable
			((Scope) parent).resolve(new PendingType<>(pendingType, this::addBound));
		}
	}

	private void addBound(CWType bound) {
		if (bound == this) {
			// TODO:
			throw new IllegalStateException("Adding self as bound");
		}
		mBounds.add(bound);
	}

	@Override
	public String getSimpleName() {
		return mName;
	}

	@Override
	public String toString() {
		return mName + (mBounds.isEmpty() ? "" :
				" extends " + mBounds.stream().map(CWType::getName).collect(Collectors.joining(" & ")));
	}
}
