package ws.codewash.java.statement;

import ws.codewash.java.Scope;
import ws.codewash.java.statement.expression.CWExpression;
import ws.codewash.parser.tree.SyntacticTreeNode;

public class CWAssertStatement extends CWStatement {
	private CWExpression mCondition;
	private CWExpression mDetailMessageExpression;

	public CWAssertStatement(SyntacticTreeNode node, Scope enclosingScope, CWExpression condition, CWExpression detailMessageExpression) {
		super(node, enclosingScope);

		mCondition = condition;
		mDetailMessageExpression = detailMessageExpression;
	}
}
