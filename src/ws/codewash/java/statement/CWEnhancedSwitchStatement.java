package ws.codewash.java.statement;

import ws.codewash.java.Location;
import ws.codewash.java.Scope;
import ws.codewash.java.statement.expression.CWExpression;
import ws.codewash.parser.tree.SyntacticTreeNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CWEnhancedSwitchStatement extends CWControlStatement {
	private CWExpression mExpression;
	private Map<List<CWExpression>, CWStatement> mCaseStatements = new HashMap<>();

	public CWEnhancedSwitchStatement(Location location, Scope enclosingScope, CWExpression expression) {
		super(location, enclosingScope);

		mExpression = expression;
	}

	public CWExpression getExpression() {
		return mExpression;
	}

	public Map<List<CWExpression>, CWStatement> getCaseStatements() {
		return mCaseStatements;
	}

	public void addCaseStatement(List<CWExpression> expressions, CWStatement statement) {
		mCaseStatements.put(expressions, statement);
	}
}
