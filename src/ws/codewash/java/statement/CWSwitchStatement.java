package ws.codewash.java.statement;

import ws.codewash.java.Scope;
import ws.codewash.parser.tree.SyntacticTreeNode;

public class CWSwitchStatement extends CWStatement {

	public CWSwitchStatement(SyntacticTreeNode node, Scope enclosingScope) {
		super(node, enclosingScope);
	}
}
