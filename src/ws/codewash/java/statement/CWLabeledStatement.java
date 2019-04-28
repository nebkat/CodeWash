package ws.codewash.java.statement;

import ws.codewash.java.Scope;

import java.util.Collections;
import java.util.List;

public class CWLabeledStatement extends CWStatement {
	private String mLabel;
	private CWStatement mStatement;

	public CWLabeledStatement(Scope enclosingScope, String label) {
		super(enclosingScope);
		mLabel = label;
	}

	public void setStatement(CWStatement statement) {
		mStatement = statement;
	}

	public String getLabel() {
		return mLabel;
	}

	@Override
	public List<CWStatement> getSubStatements() {
		return Collections.singletonList(mStatement);
	}
}
