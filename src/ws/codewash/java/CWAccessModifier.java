package ws.codewash.java;

import java.lang.reflect.Modifier;

/**
 * Access modifier
 *
 * Class to represent the access to a class, method, field, etc.
 */
public enum CWAccessModifier {
    PUBLIC, PRIVATE, PROTECTED, PACKAGE;

	/**
	 * Returns an {@link CWAccessModifier} for the given {@link Modifier} integer
	 *
	 * @param modifiers Modifiers to convert
	 * @return {@link CWAccessModifier} for {@code modifiers}
	 */
	public static CWAccessModifier forModifiers(int modifiers) {
		if (Modifier.isPublic(modifiers)) {
			return CWAccessModifier.PUBLIC;
		} else if (Modifier.isProtected(modifiers)) {
			return CWAccessModifier.PROTECTED;
		} else if (Modifier.isPrivate(modifiers)) {
			return CWAccessModifier.PRIVATE;
		} else {
			return CWAccessModifier.PACKAGE;
		}
	}
}
