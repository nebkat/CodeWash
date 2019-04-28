package ws.codewash.java.statement;

import ws.codewash.java.Scope;
import ws.codewash.java.statement.expression.CWExpression;
import ws.codewash.parser.tree.SyntacticTreeNode;

public abstract class CWSingleExpressionControlStatement extends CWControlStatement {
	private CWExpression mExpression;

	public CWSingleExpressionControlStatement(SyntacticTreeNode node, Scope enclosingScope, CWExpression expression) {
		super(node, enclosingScope);

		mExpression = expression;
	}

	public CWExpression getExpression() {
		return mExpression;
	}
}
