package ws.codewash.parser.grammar;

import ws.codewash.parser.tree.SyntacticTreeNode;
import ws.codewash.java.CompilationUnit;
import ws.codewash.parser.input.Token;
import ws.codewash.parser.tree.LexicalTreeNode;

import java.util.List;

public abstract class GrammarToken {
	public abstract List<SyntacticTreeNode> match(CompilationUnit unit, List<Token> tokens, int offset, String tree);
	public abstract LexicalTreeNode match(CompilationUnit unit, String input, int offset, String tree);

	public boolean isSyntacticallyTerminal() {
		return false;
	}
	public boolean isLexicallyTerminal() {
		return false;
	}

	public abstract String getName();

	@Override
	public String toString() {
		return getName();
	}
}
