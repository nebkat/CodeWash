package ws.codewash.java;

public abstract class CWReferenceType extends Scope implements CWType {
	public CWReferenceType() {
		super();
	}

	public CWReferenceType(Scope enclosingScope) {
		super(enclosingScope);
	}
}
