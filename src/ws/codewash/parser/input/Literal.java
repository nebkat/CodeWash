package ws.codewash.parser.input;

import ws.codewash.parser.tree.LexicalTreeNode;
import ws.codewash.util.Color;

public abstract class Literal<T> extends Token {
	Literal(LexicalTreeNode node) {
		super(node);
	}

	public abstract T getValue();

	static char parseEscapeSequence(LexicalTreeNode node) {
		return switch (node.getName()) {
			case "'\\b'" -> '\b';
			case "'\\t'" -> '\t';
			case "'\\n'" -> '\n';
			case "'\\f'" -> '\f';
			case "'\\r'" -> '\r';
			case "'\\\"'" -> '\"';
			case "`\\\\\'`" -> '\'';
			case "'\\\\'" -> '\\';
			case "OctalEscape" -> (char) Integer.parseInt(node.getContent().substring(1), 8);
			default -> throw new IllegalStateException("Unexpected " + node.getName()); // TODO
		};
	}

	public final String getTerminalType() {
		return Literal.class.getSimpleName();
	}

	@Override
	public String getDefaultPrintingColor(boolean bold) {
		return bold ? Color.PURPLE : Color.PURPLE_BOLD;
	}
}
