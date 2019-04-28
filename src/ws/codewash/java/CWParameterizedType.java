package ws.codewash.java;

import java.util.List;
import java.util.stream.Collectors;

public class CWParameterizedType extends CWReferenceType {
	private final CWClassOrInterface mClass;
	private final List<CWType> mTypes;

	public CWParameterizedType(CWClassOrInterface classOrInterface, List<CWType> types) {
		mClass = classOrInterface;
		mTypes = types;
	}

	public CWClassOrInterface getType() {
		return mClass;
	}

	@Override
	public String getSimpleName() {
		return mClass.getSimpleName() + "<" + mTypes.stream()
				.map(CWType::getSimpleName)
				.collect(Collectors.joining(", "))
				+ ">";
	}

	@Override
	public String getName() {
		return mClass.getName() + "<" + mTypes.stream()
				.map(CWType::getName)
				.collect(Collectors.joining(", "))
				+ ">";
	}

	@Override
	public String getCanonicalName() {
		return mClass.getCanonicalName() + "<" + mTypes.stream()
				.map(CWType::getCanonicalName)
				.collect(Collectors.joining(", "))
				+ ">";
	}

	@Override
	public String toString() {
		return getSimpleName();
	}
}
