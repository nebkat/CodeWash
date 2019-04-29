package ws.codewash.java.statement;

import ws.codewash.java.Location;
import ws.codewash.java.Scope;
import ws.codewash.java.statement.expression.CWExpression;
import ws.codewash.parser.tree.SyntacticTreeNode;

import java.util.Collections;
import java.util.List;

public class CWSynchronizedStatement extends CWControlStatement {
	private CWExpression mObject;
	private CWBlock mBlock;

	public CWSynchronizedStatement(Location location, Scope enclosingScope, CWExpression object) {
		super(location, enclosingScope);
		mObject = object;
	}

	public CWBlock getBlock() {
		return mBlock;
	}

	public void setBlock(CWBlock block) {
		mBlock = block;
	}

	public CWExpression getObject() {
		return mObject;
	}

	@Override
	public List<CWStatement> getSubStatements() {
		return Collections.singletonList(mBlock);
	}
}
