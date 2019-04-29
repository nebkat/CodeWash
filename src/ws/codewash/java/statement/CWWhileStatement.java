package ws.codewash.java.statement;

import ws.codewash.java.Location;
import ws.codewash.java.statement.expression.CWExpression;
import ws.codewash.java.Scope;
import ws.codewash.parser.tree.SyntacticTreeNode;

import java.util.Collections;
import java.util.List;

public class CWWhileStatement extends CWControlStatement {
	private CWExpression mCondition;
	private CWStatement mStatement;
	private boolean mDoWhileLoop;

	public CWWhileStatement(Location location, Scope enclosingScope, boolean doWhileLoop, CWExpression condition) {
		super(location, enclosingScope);
		mCondition = condition;
		mDoWhileLoop = doWhileLoop;
	}

	public CWStatement getStatement() {
		return mStatement;
	}

	public void setStatement(CWStatement statement) {
		mStatement = statement;
	}

	public CWExpression getCondition() {
		return mCondition;
	}

	public boolean isDoWhileLoop() {
		return mDoWhileLoop;
	}

	@Override
	public List<CWStatement> getSubStatements() {
		return Collections.singletonList(mStatement);
	}
}
