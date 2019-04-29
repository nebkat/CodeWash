package ws.codewash.parser.input;

import ws.codewash.parser.tree.LexicalTreeNode;
import ws.codewash.util.Color;

public class Separator extends Token {
	public Separator(LexicalTreeNode node) {
		super(node);
	}

	public final String getTerminalType() {
		return Separator.class.getSimpleName();
	}

	@Override
	public String getDefaultPrintingColor(boolean bold) {
		return !bold ? Color.WHITE : Color.WHITE_BOLD;
	}
}
