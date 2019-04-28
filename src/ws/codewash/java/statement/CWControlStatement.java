package ws.codewash.java.statement;

import ws.codewash.java.Scope;
import ws.codewash.parser.tree.SyntacticTreeNode;

public abstract class CWControlStatement extends CWStatement {
	public CWControlStatement(SyntacticTreeNode node, Scope enclosingScope) {
		super(node, enclosingScope);
	}
}
