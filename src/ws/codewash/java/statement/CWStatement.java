package ws.codewash.java.statement;

import ws.codewash.java.Scope;

public abstract class CWStatement extends Scope {
	public CWStatement(Scope enclosingScope) {
		super(enclosingScope);
	}
}
