package ws.codewash.java.statement;

import ws.codewash.java.Location;
import ws.codewash.java.Scope;
import ws.codewash.java.statement.expression.CWExpression;

public class CWAssertStatement extends CWStatement {
	private CWExpression mCondition;
	private CWExpression mDetailMessageExpression;

	public CWAssertStatement(Location location, Scope enclosingScope, CWExpression condition, CWExpression detailMessageExpression) {
		super(location, enclosingScope);

		mCondition = condition;
		mDetailMessageExpression = detailMessageExpression;
	}
}
