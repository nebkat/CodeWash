package ws.codewash.parser.grammar;

import ws.codewash.parser.tree.LexicalTreeNode;
import ws.codewash.java.CompilationUnit;
import ws.codewash.parser.tree.SyntacticTreeNode;
import ws.codewash.parser.input.Token;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OptionalGrammarToken extends GrammarToken {
	private GrammarToken mToken;

	public OptionalGrammarToken(GrammarToken token) {
		mToken = token;
	}

	@Override
	public List<SyntacticTreeNode> match(CompilationUnit unit, List<Token> tokens, int offset, String tree) {
		if (tokens.size() <= offset) {
			return Collections.emptyList();
		}

		List<SyntacticTreeNode> matches = mToken.match(unit, tokens, offset, tree);

		SyntacticTreeNode noMatch = new SyntacticTreeNode(unit, this, offset, 0);

		if (matches.isEmpty()) {
			return Collections.singletonList(noMatch);
		} else {
			SyntacticTreeNode match = matches.stream()
					.max(Comparator.comparingInt(SyntacticTreeNode::length))
					.get();

			SyntacticTreeNode parent = new SyntacticTreeNode(unit, this, offset, match.length());
			parent.addChild(match);
			return Arrays.asList(noMatch, parent);
		}
	}

	@Override
	public LexicalTreeNode match(CompilationUnit unit, String input, int offset, String tree) {
		LexicalTreeNode match = mToken.match(unit, input, offset, tree);

		if (match == null) {
			return new LexicalTreeNode(unit, this, offset, offset);
		} else {
			LexicalTreeNode parent = new LexicalTreeNode(unit, this, offset, match.getContentEnd());
			parent.addChild(match);
			return parent;
		}
	}
	@Override
	public String getName() {
		return "[" + mToken.toString() + "]";
	}
}
