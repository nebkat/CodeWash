package ws.codewash.java;

import java.lang.reflect.Modifier;

public enum CWAccessModifier {
    PUBLIC, PRIVATE, PROTECTED, PACKAGE;

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
