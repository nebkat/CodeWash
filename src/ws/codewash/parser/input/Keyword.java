package ws.codewash.parser.input;

import ws.codewash.parser.tree.LexicalTreeNode;
import ws.codewash.util.Color;

public class Keyword extends Token {
	public Keyword(LexicalTreeNode node) {
		super(node);
	}

	public final String getTerminalType() {
		return Keyword.class.getSimpleName();
	}

	@Override
	public String getDefaultPrintingColor(boolean bold) {
		return !bold ? Color.RED : Color.RED_BOLD;
	}
}
