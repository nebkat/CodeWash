package ws.codewash.parser.tree;

import ws.codewash.java.CompilationUnit;
import ws.codewash.java.Location;
import ws.codewash.parser.grammar.GrammarToken;
import ws.codewash.parser.input.Operator;
import ws.codewash.parser.input.Separator;
import ws.codewash.parser.input.Token;

import java.util.List;

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

	public void setPrintingColor(String color, String boldColor) {
		List<Token> tokens = mRawCompilationUnit.getTokens();

		for (int i = mTokenOffset; i < mTokenOffset + mTokenCount; i++) {
			Token t = tokens.get(i);
			if (!(t instanceof Operator || t instanceof Separator)) {
				tokens.get(i).setPrintingColor(color, boldColor);
			}
		}
	}

	public Location getLocation() {
		return new Location(mRawCompilationUnit,
				mRawCompilationUnit.getTokens().get(mTokenOffset).getIndex(),
				mRawCompilationUnit.getTokens().get(mTokenOffset + mTokenCount - 1).getIndex());
	}
}
