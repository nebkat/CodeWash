package ws.codewash.java.statement;

import ws.codewash.java.Location;
import ws.codewash.java.Scope;
import ws.codewash.parser.tree.SyntacticTreeNode;

import java.util.ArrayList;
import java.util.List;

public class CWTryStatement extends CWStatement {
	private List<CWCatchStatement> mCatchClauses = new ArrayList<>();
	private CWBlock mFinallyBlock;

	public CWTryStatement(Location location, Scope enclosingScope) {
		super(location, enclosingScope);
	}

	public List<CWCatchStatement> getCatchClauses() {
		return mCatchClauses;
	}

	public void addCatchClause(CWCatchStatement catchStatement) {
		mCatchClauses.add(catchStatement);
	}

	public CWBlock getFinallyBlock() {
		return mFinallyBlock;
	}

	public void setFinallyBlock(CWBlock finallyBlock) {
		mFinallyBlock = finallyBlock;
	}

	@Override
	public List<CWStatement> getSubStatements() {
		List<CWStatement> subStatements = new ArrayList<>(mCatchClauses);
		subStatements.add(mFinallyBlock);
		return subStatements;
	}
}
