package ws.codewash.java.statement.expression;

import ws.codewash.java.Location;
import ws.codewash.java.Scope;
import ws.codewash.parser.tree.SyntacticTreeNode;

public class CWUnknownExpression extends CWExpression {
	public CWUnknownExpression(Location location, Scope enclosingScope) {
		super(location, enclosingScope);
	}
}
