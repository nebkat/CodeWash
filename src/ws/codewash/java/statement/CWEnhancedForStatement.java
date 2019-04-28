package ws.codewash.java.statement;

import ws.codewash.java.statement.expression.CWExpression;
import ws.codewash.java.CWVariable;
import ws.codewash.java.Scope;
import ws.codewash.parser.tree.SyntacticTreeNode;

import java.util.Collections;
import java.util.List;

public class CWEnhancedForStatement extends CWControlStatement {
	private CWVariable mVariable;
	private CWExpression mExpression;
	private CWStatement mStatement;

	public CWEnhancedForStatement(SyntacticTreeNode node, Scope enclosingScope, CWExpression expression) {
		super(node, enclosingScope);

		mExpression = expression;
	}

	public void setVariable(CWVariable variable) {
		mVariable = variable;
	}

	public void setStatement(CWStatement statement) {
		mStatement = statement;
	}

	@Override
	public List<CWStatement> getSubStatements() {
		return Collections.singletonList(mStatement);
	}
}
