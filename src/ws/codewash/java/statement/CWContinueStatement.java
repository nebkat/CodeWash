package ws.codewash.java.statement;

import ws.codewash.java.Location;
import ws.codewash.java.Scope;
import ws.codewash.parser.tree.SyntacticTreeNode;

public class CWContinueStatement extends CWStatement {
	private String mLabel;

	public CWContinueStatement(Location location, Scope enclosingScope, String label) {
		super(location, enclosingScope);

		mLabel = label;
	}

	public String getLabel() {
		return mLabel;
	}
}
