package ws.codewash.java.statement;

import ws.codewash.java.statement.expression.CWExpression;
import ws.codewash.java.Scope;

import java.util.Arrays;
import java.util.List;

public class CWIfStatement extends CWControlStatement {
	private CWExpression mExpression;
	private CWStatement mThenStatement;
	private CWStatement mElseStatement;

	public CWIfStatement(Scope enclosingScope, CWExpression expression) {
		super(enclosingScope);

		mExpression = expression;
	}

	public void setThenStatement(CWStatement statement) {
		mThenStatement = statement;
	}

	public void setElseStatement(CWStatement statement) {
		mElseStatement = statement;
	}

	@Override
	public List<CWStatement> getSubStatements() {
		return Arrays.asList(mThenStatement, mElseStatement);
	}
}
