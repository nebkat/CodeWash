package ws.codewash.parser.input;

import ws.codewash.parser.tree.LexicalTreeNode;
import ws.codewash.util.Color;

public class Operator extends Token {
	public Operator(LexicalTreeNode node) {
		super(node);
	}

	public final String getTerminalType() {
		return Operator.class.getSimpleName();
	}

	@Override
	public String getDefaultPrintingColor(boolean bold) {
		return !bold ? Color.YELLOW : Color.YELLOW_BOLD;
	}
}
