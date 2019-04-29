package ws.codewash.parser.input;

import ws.codewash.parser.tree.LexicalTreeNode;

public class BooleanLiteral extends Literal<Boolean> {
	private final boolean mValue;

	public BooleanLiteral(LexicalTreeNode node) {
		super(node);

		mValue = Boolean.valueOf(getRawValue());
	}

	@Override
	public Boolean getValue() {
		return mValue;
	}
}
