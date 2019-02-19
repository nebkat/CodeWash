package ws.codewash.java;

public interface CWType {
	String getSimpleName();
	default String getName() {
		return getSimpleName();
	}
	default String getCanonicalName() {
		return getSimpleName();
	}

	default boolean isVoid() { return this instanceof CWVoid; }

	default boolean isPrimitive() {
		return this instanceof CWPrimitive;
	}

	default boolean isArray() {
		return this instanceof CWArray;
	}
}
