package ws.codewash.parser.input;

import ws.codewash.parser.tree.LexicalTreeNode;

public class Identifier extends Token {
	public Identifier(LexicalTreeNode node) {
		super(node);
	}

	public final String getTerminalType() {
		return Identifier.class.getSimpleName();
	}
}
