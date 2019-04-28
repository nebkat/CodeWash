package ws.codewash.java;

// TODO: BOUNDS
public enum CWWildcard implements CWType {
	WILDCARD;

	public static CWWildcard get(String keyword) {
		return keyword.equals("?") ? WILDCARD : null;
	}

	@Override
	public String getSimpleName() {
		return "?";
	}

	@Override
	public String getName() {
		return "?";
	}

	@Override
	public String getCanonicalName() {
		return "?";
	}
}
