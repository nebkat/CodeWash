package ws.codewash.java.statement;

import ws.codewash.java.CWVariable;
import ws.codewash.java.RawType;
import ws.codewash.java.Scope;
import ws.codewash.parser.tree.SyntacticTreeNode;

import java.util.List;

public class CWCatchStatement extends CWControlStatement {
	private CWVariable mVariable;
	private CWBlock mBlock;

	public CWCatchStatement(SyntacticTreeNode node, Scope enclosingScope, CWVariable variable) {
		super(node, enclosingScope);

		mVariable = variable;
	}

	public CWVariable getVariable() {
		return mVariable;
	}

	public CWBlock getBlock() {
		return mBlock;
	}

	public void setBlock(CWBlock block) {
		mBlock = block;
	}
}
