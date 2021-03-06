package ws.codewash.java.statement;

import ws.codewash.java.Location;
import ws.codewash.java.Scope;
import ws.codewash.parser.tree.SyntacticTreeNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CWBlock extends CWStatement {
	private List<CWStatement> mStatements = new ArrayList<>();

	public CWBlock(Location location, Scope enclosingScope) {
		super(location, enclosingScope);
	}

	public void addStatement(CWStatement statement) {
		mStatements.add(statement);
	}

	public List<CWStatement> getStatements() {
		return mStatements;
	}

	@Override
	public List<CWStatement> getSubStatements() {
		return mStatements;
	}
}
