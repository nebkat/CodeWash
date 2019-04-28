package ws.codewash.java.statement;

import ws.codewash.java.Scope;
import ws.codewash.java.statement.expression.CWExpression;
import ws.codewash.parser.tree.SyntacticTreeNode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CWSwitchStatement extends CWControlStatement {
	private CWExpression mExpression;
	private Map<CWExpression, List<CWStatement>> mCaseStatements = new LinkedHashMap<>();

	public CWSwitchStatement(SyntacticTreeNode node, Scope enclosingScope, CWExpression expression) {
		super(node, enclosingScope);

		mExpression = expression;
	}

	public void addCaseStatement(CWExpression expression, CWStatement statement) {
		mCaseStatements.putIfAbsent(expression, new ArrayList<>());

		mCaseStatements.get(expression).add(statement);
	}

	public CWExpression getExpression() {
		return mExpression;
	}
}
