package ws.codewash.parser.input;

import ws.codewash.parser.tree.LexicalTreeNode;
import ws.codewash.util.Color;

public class WhiteSpace extends InputElement {
	public WhiteSpace(LexicalTreeNode node) {
		super(node);
	}

	@Override
	public String getDefaultPrintingColor(boolean bold) {
		return Color.RESET;
	}
}
