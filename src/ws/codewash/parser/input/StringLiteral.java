package ws.codewash.parser.input;

import ws.codewash.parser.tree.LexicalTreeNode;

import java.util.stream.Collectors;

public class StringLiteral extends Literal<String> {
	private final String mValue;

	public StringLiteral(LexicalTreeNode node) {
		super(node);

		LexicalTreeNode valueNode = node.get("Token").get("Literal").get("StringLiteral").get(1);

		mValue = valueNode.getAll().stream()
				.map(LexicalTreeNode::get)
				.map(stringCharacterNode -> switch (stringCharacterNode.getName()) {
					case "InputCharacter" -> stringCharacterNode.getContent();
					case "EscapeSequence" -> String.valueOf(Literal.parseEscapeSequence(stringCharacterNode.get()));
					default -> throw new IllegalStateException("Unexpected " + valueNode.getName());
				}).collect(Collectors.joining());
	}

	@Override
	public String getValue() {
		return mValue;
	}
}
