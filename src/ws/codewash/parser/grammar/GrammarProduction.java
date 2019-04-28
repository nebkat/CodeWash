package ws.codewash.parser.grammar;

import ws.codewash.parser.tree.AbstractTreeNode;
import ws.codewash.parser.Parser;
import ws.codewash.parser.tree.SyntacticTreeNode;
import ws.codewash.java.CompilationUnit;
import ws.codewash.parser.Token;
import ws.codewash.parser.tree.LexicalTreeNode;
import ws.codewash.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GrammarProduction extends GrammarToken {
	private static final boolean DEBUG = Parser.DEBUG;

	private String mSymbol;
	private List<GrammarTokenSet> mAlternatives = new ArrayList<>();

	private boolean mReferenced = false;

	GrammarProduction(String symbol) {
		mSymbol = symbol;
	}

	void addAlternative(GrammarTokenSet tokenSet) {
		mAlternatives.add(tokenSet);
	}

	void setReferenced() {
		mReferenced = true;
	}

	boolean isReferenced() {
		return mReferenced;
	}

	@Override
	public List<SyntacticTreeNode> match(CompilationUnit unit, List<Token> tokens, int offset, String tree) {
		if (tokens.size() <= offset) {
			return Collections.emptyList();
		}

		// Check if the token has already been parsed by a lexical rule of the same name (e.g. "Literal" or "Identifier")
		if (isSyntacticallyTerminal()) {
			// Token type must be the same as symbol
			if (!tokens.get(offset).getType().name().equals(mSymbol)) {
				return Collections.emptyList();
			}

			// Match terminal token
			return Collections.singletonList(new SyntacticTreeNode(unit, this, offset, 1));
		}

		tree = tree + ">" + mSymbol;

		if (DEBUG) Log.w(tree, "Parsing");

		String finalTree = tree;

		List<SyntacticTreeNode> matches = mAlternatives.stream()
				.map(option -> option.match(unit, tokens, offset, finalTree))
				.flatMap(Collection::stream)
				.filter(Objects::nonNull)
				.peek(option -> option.setSymbol(this))
				.collect(Collectors.toList());

		if (mSymbol.equals("ClassBodyDeclaration") && matches.size() > 0) {
			//Log.wtf("AAAAA", matches.get(matches.size() - 1).toString(1));
		}

		return matches;
	}

	@Override
	public LexicalTreeNode match(CompilationUnit unit, String input, int offset, String tree) {
		tree = tree + ">" + mSymbol;

		String finalTree = tree;

		//Log.sleep(1000);

		//Log.w(tree, offset + " " + input.substring(offset, Math.min(input.length(), offset + 30)));
		LexicalTreeNode result;
		if (!mSymbol.equals("InputElement")) {
			result = mAlternatives.stream()
					.map(option -> option.match(unit, input, offset, finalTree))
					.filter(Objects::nonNull)
					.max(Comparator.comparingInt(AbstractTreeNode::length))
					.orElse(null);
		} else {
			result = mAlternatives.stream()
					.map(option -> option.match(unit, input, offset, finalTree))
					.filter(Objects::nonNull)
					.findFirst()
					.orElse(null);
		}


		//Log.wtf(tree, offset + " " + (result == null ? "" : result.getContent()));


		if (result != null) result.setSymbol(this);

		return result;
	}

	public String getSymbol() {
		return mSymbol;
	}

	@Override
	public boolean isSyntacticallyTerminal() {
		try {
			// Tokens are terminal symbols of syntactic grammar
			Token.Type.valueOf(mSymbol);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	@Override
	public String getName() {
		return mSymbol;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(mSymbol);
		builder.append("(");
		for (int i = 0; i < mAlternatives.size(); i++) {
			if (i > 0) {
				builder.append("|");
			}
			builder.append(mAlternatives.get(i).toString());
		}
		builder.append(")");
		return builder.toString();
	}
}
