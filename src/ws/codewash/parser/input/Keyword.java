package ws.codewash.parser.input;

import ws.codewash.parser.tree.LexicalTreeNode;

public class Keyword extends Token {
	public Keyword(LexicalTreeNode node) {
		super(node);
	}

	public final String getTerminalType() {
		return Keyword.class.getSimpleName();
	}
}
