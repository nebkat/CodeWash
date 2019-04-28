package ws.codewash.java.statement;

import ws.codewash.java.CWVariable;
import ws.codewash.java.Scope;
import ws.codewash.java.statement.CWStatement;
import ws.codewash.parser.tree.SyntacticTreeNode;

public class CWLocalVariableDeclarationStatement extends CWStatement {
	private CWVariable mLocalVariable;

	public CWLocalVariableDeclarationStatement(SyntacticTreeNode node, Scope enclosingScope, CWVariable localVariable) {
		super(node, enclosingScope);

		enclosingScope.addLocalVariableDeclaration(localVariable);
	}

	public CWVariable getLocalVariable() {
		return mLocalVariable;
	}
}
