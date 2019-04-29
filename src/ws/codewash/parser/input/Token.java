package ws.codewash.parser.input;

import ws.codewash.parser.tree.LexicalTreeNode;

public abstract class Token extends InputElement {
	Token(LexicalTreeNode node) {
		super(node);
	}

	public abstract String getTerminalType();
}
