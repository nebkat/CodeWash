package ws.codewash.java.statement;

import ws.codewash.java.Scope;
import ws.codewash.java.statement.expression.CWExpression;
import ws.codewash.parser.tree.SyntacticTreeNode;

public class CWThrowStatement extends CWSingleExpressionControlStatement {
	public CWThrowStatement(SyntacticTreeNode node, Scope enclosingScope, CWExpression expression) {
		super(node, enclosingScope, expression);
	}
}
