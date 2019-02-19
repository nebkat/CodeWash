package ws.codewash.parser.tree;

import ws.codewash.parser.CompilationUnit;
import ws.codewash.parser.grammar.GrammarToken;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LexicalTreeNode extends AbstractTreeNode<LexicalTreeNode> {
	public LexicalTreeNode(CompilationUnit rawCompilationUnit, GrammarToken token, int contentStart, int contentEnd) {
		super(rawCompilationUnit, token, contentStart, contentEnd);
	}

	@Override
	public boolean isTerminal() {
		return mGrammarToken.isLexicallyTerminal();
	}
}
