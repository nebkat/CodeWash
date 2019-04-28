package ws.codewash.parser.tree;

import ws.codewash.java.CompilationUnit;
import ws.codewash.parser.grammar.GrammarToken;

public class LexicalTreeNode extends AbstractTreeNode<LexicalTreeNode> {
	public LexicalTreeNode(CompilationUnit rawCompilationUnit, GrammarToken token, int contentStart, int contentEnd) {
		super(rawCompilationUnit, token, contentStart, contentEnd);
	}

	@Override
	public boolean isTerminal() {
		return mGrammarToken.isLexicallyTerminal();
	}
}
