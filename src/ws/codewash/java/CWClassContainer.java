package ws.codewash.java;

public interface CWClassContainer {
    CWClassContainer getContainer();

	default boolean isPackage() {
		return this instanceof CWPackage;
	}
}
