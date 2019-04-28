package ws.codewash.java.statement.expression;

import ws.codewash.java.Scope;
import ws.codewash.java.statement.CWStatement;
import ws.codewash.parser.tree.SyntacticTreeNode;

public abstract class CWExpression extends CWStatement {
	public CWExpression(SyntacticTreeNode node, Scope enclosingScope) {
		super(node, enclosingScope);
	}
}
