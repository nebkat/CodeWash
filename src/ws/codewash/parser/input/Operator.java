package ws.codewash.parser.input;

import ws.codewash.parser.tree.LexicalTreeNode;

public class Operator extends Token {
	public Operator(LexicalTreeNode node) {
		super(node);
	}

	public final String getTerminalType() {
		return Operator.class.getSimpleName();
	}
}
