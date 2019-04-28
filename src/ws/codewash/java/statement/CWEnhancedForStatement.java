package ws.codewash.java.statement;

import ws.codewash.java.statement.expression.CWExpression;
import ws.codewash.java.CWVariable;
import ws.codewash.java.Scope;

public class CWEnhancedForStatement extends CWControlStatement {
	private CWVariable mVariable;
	private CWExpression mExpression;
	private CWStatement mStatement;

	public CWEnhancedForStatement(Scope enclosingScope, CWExpression expression) {
		super(enclosingScope);

		mExpression = expression;
	}

	public void setVariable(CWVariable variable) {
		mVariable = variable;
	}

	public void setStatement(CWStatement statement) {
		mStatement = statement;
	}
}
