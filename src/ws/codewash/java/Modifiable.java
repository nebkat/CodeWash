package ws.codewash.java;

import java.lang.reflect.Modifier;

public interface Modifiable {
	int getModifiers();

	default CWAccessModifier getAccess() {
		return CWAccessModifier.forModifiers(getModifiers());
	}

	default boolean isFinal() {
		return Modifier.isFinal(getModifiers());
	}

	default boolean isStatic() {
		return Modifier.isStatic(getModifiers());
	}

	default boolean isAbstract() {
		return Modifier.isFinal(getModifiers());
	}

	default String getModifiersForToString() {
		return getModifiers() > 0 ? Modifier.toString(getModifiers()) + " " : "";
	}
}
