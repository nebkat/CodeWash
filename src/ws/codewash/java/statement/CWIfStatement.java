package ws.codewash.java.statement;

import ws.codewash.java.statement.expression.CWExpression;
import ws.codewash.java.Scope;
import ws.codewash.parser.tree.SyntacticTreeNode;

import java.util.Arrays;
import java.util.List;

public class CWIfStatement extends CWControlStatement {
	private CWExpression mExpression;
	private CWStatement mThenStatement;
	private CWStatement mElseStatement;

	public CWIfStatement(SyntacticTreeNode node, Scope enclosingScope, CWExpression expression) {
		super(node, enclosingScope);

		mExpression = expression;
	}

	public void setThenStatement(CWStatement statement) {
		mThenStatement = statement;
	}

	public void setElseStatement(CWStatement statement) {
		mElseStatement = statement;
	}

	public CWStatement getElseStatement() {
		return mElseStatement;
	}

	public CWStatement getThenStatement() {
		return mThenStatement;
	}

	@Override
	public List<CWStatement> getSubStatements() {
		return Arrays.asList(mThenStatement, mElseStatement);
	}
}
