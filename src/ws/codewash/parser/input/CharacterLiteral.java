package ws.codewash.parser.input;

import ws.codewash.parser.tree.LexicalTreeNode;

public class CharacterLiteral extends Literal<Character> {
	private final char mValue;

	public CharacterLiteral(LexicalTreeNode node) {
		super(node);

		LexicalTreeNode valueNode = node.get("Token").get("Literal").get("CharacterLiteral").get(1);

		mValue = switch (valueNode.getName()) {
			case "EscapeSequence" -> Literal.parseEscapeSequence(valueNode.get());
			case "SingleCharacter" -> getRawValue().charAt(0);
			default -> throw new IllegalStateException("Unexpected " + valueNode.getName());
		};
	}

	@Override
	public Character getValue() {
		return mValue;
	}
}
