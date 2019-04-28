package ws.codewash.java.statement;

import ws.codewash.java.Scope;
import ws.codewash.java.statement.expression.CWExpression;
import ws.codewash.parser.tree.SyntacticTreeNode;

import java.util.List;
import java.util.Map;

public class CWEnhancedSwitchStatement extends CWControlStatement {
	private CWExpression mExpression;
	private Map<List<CWExpression>, CWExpression> mCaseExpressions;

	public CWEnhancedSwitchStatement(SyntacticTreeNode node, Scope enclosingScope, CWExpression expression, Map<List<CWExpression>, CWExpression> caseExpressions) {
		super(node, enclosingScope);

		mExpression = expression;
		mCaseExpressions = caseExpressions;
	}

	public CWExpression getExpression() {
		return mExpression;
	}

	public Map<List<CWExpression>, CWExpression> getCaseExpressions() {
		return mCaseExpressions;
	}
}
