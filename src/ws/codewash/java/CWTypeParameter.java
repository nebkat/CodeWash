package ws.codewash.java;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CWTypeParameter implements CWType {
	private String mName;
	private Set<CWClassOrInterface> mBounds = new HashSet<>();

	public CWTypeParameter(Scope enclosingScope, String name, List<String> bounds) {
		mName = name;

		for (String pendingType : bounds) {
			enclosingScope.resolve(new PendingType<>(pendingType, this::addBound));
		}
	}

	private void addBound(CWClassOrInterface bound) {
		mBounds.add(bound);
	}

	public String getVariableName() {
		return mName;
	}

	@Override
	public String getSimpleName() {
		return mName + extendsString(CWType::getSimpleName);
	}

	@Override
	public String getName() {
		return mName + extendsString(CWType::getName);
	}

	@Override
	public String getCanonicalName() {
		return getSimpleName();
	}

	private String extendsString(Function<CWClassOrInterface, String> nameMethod) {
		return mBounds.isEmpty() ? "" :
				" extends" + mBounds.stream().map(nameMethod).collect(Collectors.joining(" & "));
	}
}
