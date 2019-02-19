package ws.codewash.parser.grammar;

import ws.codewash.parser.Parser;
import ws.codewash.parser.tree.SyntacticTreeNode;
import ws.codewash.parser.CompilationUnit;
import ws.codewash.parser.Token;
import ws.codewash.parser.tree.LexicalTreeNode;
import ws.codewash.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GrammarTokenSet extends GrammarToken {
	private static final boolean DEBUG = Parser.DEBUG;

	private String mName;
	private List<GrammarToken> mTokens = new ArrayList<>();
	private GrammarToken mNotCondition = null;

	void setName(String name) {
		mName = name;
	}

	void addToken(GrammarToken token) {
		mTokens.add(token);
	}

	void setNotCondition(GrammarToken notCondition) {
		mNotCondition = notCondition;
	}

	List<GrammarToken> getTokens() {
		return mTokens;
	}

	@Override
	public List<SyntacticTreeNode> match(CompilationUnit unit, List<Token> tokens, int offset, String tree) {
		List<Integer> currentOffsets = new ArrayList<>();
		currentOffsets.add(0);

		Map<GrammarToken, List<SyntacticTreeNode>> tokenMatches = new HashMap<>();

		for (GrammarToken token : mTokens) {
			tokenMatches.put(token, new ArrayList<>());

			List<Integer> newOffsets = new ArrayList<>();
			for (Integer localOffset : currentOffsets) {
				List<SyntacticTreeNode> matches = token.match(unit, tokens, offset + localOffset, tree);

				if (matches.isEmpty()) {
					continue;
				}

				for (SyntacticTreeNode match : matches) {
					if (newOffsets.contains(localOffset + match.length())) continue;

					if (DEBUG) Log.i("GTS/" + tree + ">>" + getName(), token.toString() + " " + match.getTokenOffset() + " " + match.getNextTokenOffset() + " " + match.getContent());

					tokenMatches.get(token).add(match);
					newOffsets.add(localOffset + match.length());
				}
			}

			if (newOffsets.isEmpty()) {
				if (tokens.size() <= currentOffsets.get(0) + offset) {
					if (DEBUG) Log.d("GTS/" + tree + ">>" + getName(), "Could not find " + token.toString() + " at EOF");
				} else {
					if (DEBUG) Log.d("GTS/" + tree + ">>" + getName(), "Could not find " + token.toString() + " at " + tokens.get(currentOffsets.get(0) + offset));
				}

				return Collections.emptyList();
			}

			currentOffsets = newOffsets;
		}

		for (GrammarToken token : mTokens) {
			for (SyntacticTreeNode match : tokenMatches.get(token)) {
				if (DEBUG) Log.w(tree, token.toString() + " " + match.getTokenOffset() + " " + match.getNextTokenOffset() + " " + match.getContent());
			}
		}

		List<List<SyntacticTreeNode>> potentialMatches = new ArrayList<>();
		for (SyntacticTreeNode node : tokenMatches.get(mTokens.get(0))) {
			List<SyntacticTreeNode> potentialMatch = new ArrayList<>();
			potentialMatch.add(node);
			potentialMatches.add(potentialMatch);
		}

		for (int i = 1; i < mTokens.size(); i++) {
			List<List<SyntacticTreeNode>> newPotentialMatches = new ArrayList<>();
			for (List<SyntacticTreeNode> potentialMatch : potentialMatches) {
				SyntacticTreeNode lastMatch = potentialMatch.get(potentialMatch.size() - 1);

				for (SyntacticTreeNode potentialNextMatch : tokenMatches.get(mTokens.get(i))) {
					if (lastMatch.getNextTokenOffset() == potentialNextMatch.getTokenOffset()) {
						List<SyntacticTreeNode> newPotentialMatch = new ArrayList<>(potentialMatch);
						newPotentialMatch.add(potentialNextMatch);
						newPotentialMatches.add(newPotentialMatch);
					}
				}
			}
			potentialMatches = newPotentialMatches;
		}

		List<SyntacticTreeNode> finalMatches = new ArrayList<>();

		for (List<SyntacticTreeNode> matches : potentialMatches) {
			SyntacticTreeNode lastMatch = matches.get(matches.size() - 1);

			SyntacticTreeNode parent = new SyntacticTreeNode(unit, this, offset,
					lastMatch.getNextTokenOffset() - offset);
			parent.addChildren(matches);

			if (DEBUG) Log.e("GTS/" + tree + ">>" + getName(), parent.getContent());

			finalMatches.add(parent);
		}

		// Check for not condition (e.g. identifier but not "var")
		if (mNotCondition != null) {
			List<SyntacticTreeNode> notConditionMatches = mNotCondition.match(unit, tokens, offset, tree);
			for (SyntacticTreeNode notConditionMatch : notConditionMatches) {
				finalMatches.removeIf(match -> notConditionMatch.length() == match.length());
			}
		}

		return finalMatches;
	}

	@Override
	public LexicalTreeNode match(CompilationUnit unit, String input, int offset, String tree) {
		List<LexicalTreeNode> childNodes = new ArrayList<>();

		int localOffset = 0;
		for (GrammarToken token : mTokens) {
			LexicalTreeNode childNode = token.match(unit, input, offset + localOffset, tree);

			if (childNode == null) {
				return null;
			}

			localOffset += childNode.length();

			childNodes.add(childNode);
		}

		LexicalTreeNode parent = new LexicalTreeNode(unit, this, offset, offset + localOffset);
		parent.addChildren(childNodes);

		// Check for not condition (e.g. identifier but not "var")
		if (mNotCondition != null) {
			LexicalTreeNode notConditionMatch = mNotCondition.match(unit, input, offset, tree);
			if (notConditionMatch != null && notConditionMatch.length() >= parent.length()) {
				return null;
			}
		}

		return parent;
	}

	@Override
	public String getName() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < mTokens.size(); i++) {
			if (i > 0) {
				builder.append(" ");
			}
			builder.append(mTokens.get(i).toString());
		}
		return builder.toString();
	}
}
