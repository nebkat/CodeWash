package ws.codewash.parser.input;

import ws.codewash.parser.tree.LexicalTreeNode;

public class FloatLiteral extends Literal<Float> {
	private final float mValue;

	public FloatLiteral(LexicalTreeNode node) {
		super(node);

		mValue = Float.parseFloat(getRawValue());
	}

	@Override
	public Float getValue() {
		return mValue;
	}
}
