package ws.codewash.java;

public interface TypeResolver {
	void resolve(PendingType pendingType);
	CWType resolveImmediate(String typeName);
}
