package ws.codewash.parser.tree;

import ws.codewash.java.CompilationUnit;
import ws.codewash.parser.grammar.GrammarToken;

public class SyntacticTreeNode extends AbstractTreeNode<SyntacticTreeNode> {
	private int mTokenOffset;
	private int mTokenCount;

	public SyntacticTreeNode(CompilationUnit rawCompilationUnit, GrammarToken token, int offset, int length) {
		super(rawCompilationUnit, token);

		int contentStart = rawCompilationUnit.getTokens().get(offset).getStart();
		int contentEnd = length == 0 ? contentStart : rawCompilationUnit.getTokens().get(offset + length - 1).getEnd();

		setContentRange(contentStart, contentEnd);

		mTokenOffset = offset;
		mTokenCount = length;
	}

	@Override
	public int length() {
		return mTokenCount;
	}

	@Override
	public boolean isTerminal() {
		return (mSymbol != null && mSymbol.isSyntacticallyTerminal()) || mGrammarToken.isSyntacticallyTerminal();
	}

	public int getTokenOffset() {
		return mTokenOffset;
	}

	public int getNextTokenOffset() {
		return mTokenOffset + mTokenCount;
	}
}
