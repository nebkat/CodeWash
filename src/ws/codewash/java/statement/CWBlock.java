package ws.codewash.java.statement;

import ws.codewash.java.Scope;

import java.util.ArrayList;
import java.util.List;

public class CWBlock extends CWStatement {
	private List<CWStatement> mStatements = new ArrayList<>();

	public CWBlock(Scope enclosingScope) {
		super(enclosingScope);
	}

	public void addStatement(CWStatement statement) {
		mStatements.add(statement);
	}

	public List<CWStatement> getStatements() {
		return mStatements;
	}
}
