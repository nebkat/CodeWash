package ws.codewash.java.statement.expression;

import ws.codewash.java.Location;
import ws.codewash.java.Scope;
import ws.codewash.java.statement.CWStatement;
import ws.codewash.parser.tree.SyntacticTreeNode;

public abstract class CWExpression extends CWStatement {
	public CWExpression(Location location, Scope enclosingScope) {
		super(location, enclosingScope);
	}
}
