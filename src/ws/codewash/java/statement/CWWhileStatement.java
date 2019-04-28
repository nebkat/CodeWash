package ws.codewash.java.statement;

import ws.codewash.java.statement.expression.CWExpression;
import ws.codewash.java.Scope;

public class CWWhileStatement extends CWControlStatement {
	private CWExpression mCondition;
	private CWStatement mStatement;
	private boolean mDoWhileLoop;

	public CWWhileStatement(Scope enclosingScope, boolean doWhileLoop, CWExpression condition) {
		super(enclosingScope);
		mDoWhileLoop = doWhileLoop;
		mCondition = condition;
	}

	public void setStatement(CWStatement statement) {
		mStatement = statement;
	}
}
