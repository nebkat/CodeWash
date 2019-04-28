package ws.codewash.java.statement.expression;

import ws.codewash.java.Scope;
import ws.codewash.parser.tree.SyntacticTreeNode;

public class CWUnknownExpression extends CWExpression {
	public CWUnknownExpression(SyntacticTreeNode node, Scope enclosingScope) {
		super(node, enclosingScope);
	}
}
