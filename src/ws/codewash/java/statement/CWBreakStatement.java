package ws.codewash.java.statement;

import ws.codewash.java.Location;
import ws.codewash.java.Scope;
import ws.codewash.java.statement.expression.CWExpression;
import ws.codewash.parser.tree.SyntacticTreeNode;

public class CWBreakStatement extends CWSingleExpressionControlStatement {
	public CWBreakStatement(Location location, Scope enclosingScope, CWExpression expression) {
		super(location, enclosingScope, expression);
	}
}
