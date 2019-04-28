package ws.codewash.java.statement.expression;

import ws.codewash.java.Scope;
import ws.codewash.java.statement.CWStatement;

public abstract class CWExpression extends CWStatement {
	public CWExpression(Scope enclosingScope) {
		super(enclosingScope);
	}
}
