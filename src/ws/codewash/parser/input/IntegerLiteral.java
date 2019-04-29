package ws.codewash.parser.input;

import ws.codewash.parser.tree.LexicalTreeNode;

public class IntegerLiteral extends Literal<Integer> {
	private int mValue;

	public IntegerLiteral(LexicalTreeNode node) {
		super(node);

		LexicalTreeNode valueNode = node.get("Token").get("Literal").get("IntegerLiteral").get();

		mValue = switch (valueNode.getName()) {
			case "DecimalIntegerLiteral" -> Integer.parseInt(valueNode.get("DecimalNumeral").getContent());
			case "HexIntegerLiteral" -> Integer.parseInt(valueNode.get("HexNumeral").getContent().substring(2), 16);
			case "OctalIntegerLiteral" -> Integer.parseInt(valueNode.get("OctalNumeral").getContent().substring(1), 8);
			case "BinaryIntegerLiteral" -> Integer.parseInt(valueNode.get("BinaryNumeral").getContent().substring(2), 2);
			default -> throw new IllegalStateException("Unexpected " + valueNode.getName());
		};
	}

	@Override
	public Integer getValue() {
		return mValue;
	}
}
