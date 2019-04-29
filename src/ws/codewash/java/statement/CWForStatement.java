package ws.codewash.java.statement;

import ws.codewash.java.CWVariable;
import ws.codewash.java.Location;
import ws.codewash.java.Scope;
import ws.codewash.java.statement.expression.CWExpression;
import ws.codewash.parser.tree.SyntacticTreeNode;

import java.util.Arrays;
import java.util.List;

public class CWForStatement extends CWControlStatement {
	private CWVariable mVariable;
	private CWLocalVariableDeclarationStatement mInitVariableDeclarationStatement;
	private List<CWExpression> mInitExpressions;
	private CWExpression mCondition;
	private List<CWExpression> mUpdateExpressions;
	private CWStatement mStatement;

	public CWForStatement(Location location, Scope enclosingScope) {
		super(location, enclosingScope);
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

	public void setStatement(CWStatement statement) {
		mStatement = statement;
	}

	@Override
	public List<CWStatement> getSubStatements() {
		return Arrays.asList(mInitVariableDeclarationStatement, mStatement);
	}
}
