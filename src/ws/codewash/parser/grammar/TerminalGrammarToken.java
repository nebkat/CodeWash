package ws.codewash.parser.grammar;

import ws.codewash.parser.tree.SyntacticTreeNode;
import ws.codewash.java.CompilationUnit;
import ws.codewash.parser.input.Token;
import ws.codewash.parser.tree.LexicalTreeNode;

import java.util.Collections;
import java.util.List;

public class TerminalGrammarToken extends GrammarToken {
	private String mValue;

	TerminalGrammarToken(String value) {
		mValue = value;
	}

	@Override
	public List<SyntacticTreeNode> match(CompilationUnit unit, List<Token> tokens, int offset, String tree) {
		if (tokens.size() <= offset) {
			return Collections.emptyList();
		}

		if (tokens.get(offset).getRawValue().equals(mValue)) {
			return Collections.singletonList(new SyntacticTreeNode(unit, this, offset, 1));
		}

		return Collections.emptyList();
	}

	@Override
	public LexicalTreeNode match(CompilationUnit unit, String input, int offset, String tree) {
		if (input.regionMatches(offset, mValue, 0, mValue.length())) {
			return new LexicalTreeNode(unit, this, offset, offset + mValue.length());
		}

		return null;
	}

	@Override
	public boolean isSyntacticallyTerminal() {
		return true;
	}

	@Override
	public boolean isLexicallyTerminal() {
		return true;
	}

	@Override
	public String getName() {
		return "'" + mValue + "'";
	}
}
