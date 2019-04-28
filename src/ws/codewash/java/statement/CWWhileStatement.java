package ws.codewash.java.statement;

import ws.codewash.java.statement.expression.CWExpression;
import ws.codewash.java.Scope;
import ws.codewash.parser.tree.SyntacticTreeNode;

import java.util.Collections;
import java.util.List;

public class CWWhileStatement extends CWControlStatement {
	private CWExpression mCondition;
	private CWStatement mStatement;
	private boolean mDoWhileLoop;

	public CWWhileStatement(SyntacticTreeNode node, Scope enclosingScope, boolean doWhileLoop, CWExpression condition) {
		super(node, enclosingScope);
		mDoWhileLoop = doWhileLoop;
		mCondition = condition;
	}

	public void setStatement(CWStatement statement) {
		mStatement = statement;
	}

	@Override
	public List<CWStatement> getSubStatements() {
		return Collections.singletonList(mStatement);
	}
}
