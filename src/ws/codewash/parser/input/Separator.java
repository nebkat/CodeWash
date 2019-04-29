package ws.codewash.parser.input;

import ws.codewash.parser.tree.LexicalTreeNode;

public class Separator extends Token {
	public Separator(LexicalTreeNode node) {
		super(node);
	}

	public final String getTerminalType() {
		return Separator.class.getSimpleName();
	}
}
