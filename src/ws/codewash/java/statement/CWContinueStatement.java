package ws.codewash.java.statement;

import ws.codewash.java.Scope;
import ws.codewash.parser.tree.SyntacticTreeNode;

public class CWContinueStatement extends CWStatement {
	private String mLabel;

	public CWContinueStatement(SyntacticTreeNode node, Scope enclosingScope, String label) {
		super(node, enclosingScope);

		mLabel = label;
	}

	public String getLabel() {
		return mLabel;
	}
}
