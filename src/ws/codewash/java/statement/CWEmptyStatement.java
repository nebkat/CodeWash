package ws.codewash.java.statement;

import ws.codewash.java.Scope;
import ws.codewash.parser.tree.SyntacticTreeNode;

public class CWEmptyStatement extends CWStatement {
	public CWEmptyStatement(SyntacticTreeNode node, Scope enclosingScope) {
		super(node, enclosingScope);
	}
}
