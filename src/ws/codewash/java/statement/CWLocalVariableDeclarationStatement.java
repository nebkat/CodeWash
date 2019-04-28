package ws.codewash.java.statement;

import ws.codewash.java.CWVariable;
import ws.codewash.java.Scope;
import ws.codewash.java.statement.CWStatement;

public class CWLocalVariableDeclarationStatement extends CWStatement {
	private CWVariable mLocalVariable;

	public CWLocalVariableDeclarationStatement(Scope enclosingScope, CWVariable localVariable) {
		super(enclosingScope);

		enclosingScope.addLocalVariableDeclaration(localVariable);
	}

	public CWVariable getLocalVariable() {
		return mLocalVariable;
	}
}
