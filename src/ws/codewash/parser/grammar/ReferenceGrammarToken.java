package ws.codewash.parser.grammar;

import ws.codewash.parser.tree.SyntacticTreeNode;
import ws.codewash.java.CompilationUnit;
import ws.codewash.parser.input.Token;
import ws.codewash.parser.tree.LexicalTreeNode;

import java.util.List;

public class ReferenceGrammarToken extends GrammarToken {
	private String mSymbol;
	private GrammarProduction mProduction;

	ReferenceGrammarToken(String symbol) {
		mSymbol = symbol;
	}

	void resolveSymbol(Grammar grammar) {
		mProduction = grammar.getProduction(mSymbol);

		if (mProduction == null) {
			// TODO: Handle
			throw new IllegalStateException("Could not resolve symbol " + mSymbol);
		}

		mProduction.setReferenced();
	}

	@Override
	public List<SyntacticTreeNode> match(CompilationUnit unit, List<Token> tokens, int offset, String tree) {
		return mProduction.match(unit, tokens, offset, tree);
	}

	@Override
	public LexicalTreeNode match(CompilationUnit unit, String input, int offset, String tree) {
		return mProduction.match(unit, input, offset, tree);
	}

	@Override
	public boolean isSyntacticallyTerminal() {
		return mProduction.isSyntacticallyTerminal();
	}

	@Override
	public String getName() {
		return mSymbol;
	}
}