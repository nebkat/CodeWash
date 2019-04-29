package ws.codewash.parser.input;

import ws.codewash.parser.tree.LexicalTreeNode;

public class NullLiteral extends Literal<Void> {
	public NullLiteral(LexicalTreeNode node) {
		super(node);
	}

	@Override
	public Void getValue() {
		return null;
	}
}
