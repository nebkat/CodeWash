package ws.codewash.java.statement;

import ws.codewash.java.CWVariable;
import ws.codewash.java.Location;
import ws.codewash.java.Scope;
import ws.codewash.java.statement.CWStatement;
import ws.codewash.parser.tree.SyntacticTreeNode;

public class CWLocalVariableDeclarationStatement extends CWStatement {
	private CWVariable mLocalVariable;

	public CWLocalVariableDeclarationStatement(Location location, Scope enclosingScope, CWVariable localVariable) {
		super(location, enclosingScope);

		mLocalVariable = localVariable;

		enclosingScope.addLocalVariableDeclaration(localVariable);
	}

	public CWVariable getLocalVariable() {
		return mLocalVariable;
	}
}
