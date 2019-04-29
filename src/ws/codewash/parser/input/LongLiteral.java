package ws.codewash.parser.input;

import ws.codewash.parser.tree.LexicalTreeNode;

public class LongLiteral extends NumberLiteral<Long> {
	private long mValue;

	public LongLiteral(LexicalTreeNode node) {
		super(node);

		LexicalTreeNode valueNode = node.get("Token").get("Literal").get("IntegerLiteral").get();

		mValue = switch (valueNode.getName()) {
			case "DecimalIntegerLiteral" -> Long.parseLong(valueNode.get("DecimalNumeral").getContent());
			case "HexIntegerLiteral" -> Long.parseLong(valueNode.get("HexNumeral").getContent().substring(2), 16);
			case "OctalIntegerLiteral" -> Long.parseLong(valueNode.get("OctalNumeral").getContent().substring(1), 8);
			case "BinaryIntegerLiteral" -> Long.parseLong(valueNode.get("BinaryNumeral").getContent().substring(2), 2);
			default -> throw new IllegalStateException("Unexpected " + valueNode.getName());
		};
	}

	@Override
	public Long getValue() {
		return mValue;
	}
}
