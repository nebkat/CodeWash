package ws.codewash.java;

public interface CWType {
	default boolean isPrimitive() {
		return this instanceof CWPrimitive;
	}

	default boolean isArray() {
		return this instanceof CWArray;
	}
}
