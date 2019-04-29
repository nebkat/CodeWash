package ws.codewash.parser.input;

import ws.codewash.parser.tree.LexicalTreeNode;

public class DoubleLiteral extends NumberLiteral<Double> {
	private final double mValue;

	public DoubleLiteral(LexicalTreeNode node) {
		super(node);

		mValue = Double.parseDouble(getRawValue());
	}

	@Override
	public Double getValue() {
		return mValue;
	}
}
