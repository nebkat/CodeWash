package ws.codewash.parser.input;

import ws.codewash.parser.tree.LexicalTreeNode;

public abstract class NumberLiteral<T extends Number> extends Literal<T> {
	NumberLiteral(LexicalTreeNode node) {
		super(node);
	}
}
