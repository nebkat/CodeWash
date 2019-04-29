package ws.codewash.parser.input;

import ws.codewash.parser.tree.LexicalTreeNode;
import ws.codewash.util.Color;

public class Comment extends InputElement {
	public Comment(LexicalTreeNode node) {
		super(node);
	}

	@Override
	public String getDefaultPrintingColor(boolean bold) {
		return !bold ? Color.BLACK : Color.BLACK_BOLD;
	}
}
