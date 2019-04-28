package ws.codewash.java.statement;

import ws.codewash.java.CWVariable;
import ws.codewash.java.Scope;
import ws.codewash.java.statement.expression.CWExpression;

import java.util.List;

public class CWBasicForStatement extends CWControlStatement {
	private CWVariable mVariable;
	private CWLocalVariableDeclarationStatement mInitVariableDeclarationStatement;
	private List<CWExpression> mInitExpressions;
	private CWExpression mCondition;
	private List<CWExpression> mUpdateExpressions;

	public CWBasicForStatement(Scope enclosingScope) {
		super(enclosingScope);
	}

	public void setVariable(CWVariable variable) {
		mVariable = variable;
	}

	public void setInitExpressions(List<CWExpression> initExpressions) {
		mInitExpressions = initExpressions;
	}

	public void setInitVariableDeclarationStatement(CWLocalVariableDeclarationStatement initVariableDeclarationStatement) {
		mInitVariableDeclarationStatement = initVariableDeclarationStatement;
		mVariable = initVariableDeclarationStatement.getLocalVariable();
	}

	public void setCondition(CWExpression condition) {
		mCondition = condition;
	}

	public void setUpdateExpressions(List<CWExpression> updateExpressions) {
		mUpdateExpressions = updateExpressions;
	}
}
