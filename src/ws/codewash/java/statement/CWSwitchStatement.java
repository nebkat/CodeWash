package ws.codewash.java.statement;

import ws.codewash.java.Location;
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

	public CWSwitchStatement(Location location, Scope enclosingScope, CWExpression expression) {
		super(location, enclosingScope);

		mExpression = expression;
	}

	public void addCaseExpression(CWExpression expression) {
		if (mCaseStatements.containsKey(expression)) {
			// TODO
			throw new IllegalStateException("Duplicate case expresion " + expression);
		}

		mCaseStatements.put(expression, new ArrayList<>());
	}

	public void addCaseStatement(CWExpression expression, CWStatement statement) {
		mCaseStatements.get(expression).add(statement);
	}

	public CWExpression getExpression() {
		return mExpression;
	}
}
