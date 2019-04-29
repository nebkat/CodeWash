package ws.codewash.parser.input;

import ws.codewash.parser.tree.LexicalTreeNode;
import ws.codewash.util.Color;

public class Identifier extends Token {
	public Identifier(LexicalTreeNode node) {
		super(node);
	}

	public final String getTerminalType() {
		return Identifier.class.getSimpleName();
	}

	@Override
	public String getDefaultPrintingColor(boolean bold) {
		return !bold ? Color.GREEN : Color.GREEN_BOLD;
	}
}
