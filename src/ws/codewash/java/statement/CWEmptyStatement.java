package ws.codewash.java.statement;

import ws.codewash.java.Location;
import ws.codewash.java.Scope;
import ws.codewash.parser.tree.SyntacticTreeNode;

public class CWEmptyStatement extends CWStatement {
	public CWEmptyStatement(Location location, Scope enclosingScope) {
		super(location, enclosingScope);
	}
}
