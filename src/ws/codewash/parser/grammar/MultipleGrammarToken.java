package ws.codewash.parser.grammar;

import ws.codewash.parser.tree.LexicalTreeNode;
import ws.codewash.parser.CompilationUnit;
import ws.codewash.parser.tree.SyntacticTreeNode;
import ws.codewash.parser.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MultipleGrammarToken extends GrammarToken {
	private GrammarToken mToken;

	public MultipleGrammarToken(GrammarToken token) {
		mToken = token;
	}

	public List<SyntacticTreeNode> match2(CompilationUnit unit, List<Token> tokens, int offset, String tree) {
		List<SyntacticTreeNode> parents = new ArrayList<>();
		parents.add(new SyntacticTreeNode(unit, this, offset, 0));
		List<List<SyntacticTreeNode>> childrenNodes = new ArrayList<>();

		List<SyntacticTreeNode> matches = mToken.match(unit, tokens, offset, tree);
		if (matches.isEmpty()) {
			return parents;
		}

		for (SyntacticTreeNode match : matches) {
			childrenNodes.add(Collections.singletonList(match));

			SyntacticTreeNode parent = new SyntacticTreeNode(unit, this, offset, match.length());
			parent.addChild(match);
			parents.add(parent);
		}

		while (!childrenNodes.isEmpty()) {
			List<List<SyntacticTreeNode>> newChildrenNodes = new ArrayList<>();
			List<Integer> visitedOffsets = new ArrayList<>();
			for (List<SyntacticTreeNode> childrenNode : childrenNodes) {
				SyntacticTreeNode lastMatch = childrenNode.get(childrenNode.size() - 1);
				if (visitedOffsets.contains(lastMatch.getNextTokenOffset())) {
					continue;
				}

				visitedOffsets.add(lastMatch.getNextTokenOffset());

				List<SyntacticTreeNode> nextMatches = mToken.match(unit, tokens, lastMatch.getNextTokenOffset(), tree);
				if (nextMatches.isEmpty()) {
					continue;
				}

				for (SyntacticTreeNode nextMatch : nextMatches) {
					if (nextMatch.isEmpty()) {
						continue;
					}

					List<SyntacticTreeNode> newChildrenNode = new ArrayList<>(childrenNode);
					newChildrenNode.add(nextMatch);

					SyntacticTreeNode parent = new SyntacticTreeNode(unit, this, offset, nextMatch.getNextTokenOffset() - offset);
					parent.addChildren(newChildrenNode);
					parents.add(parent);

					newChildrenNodes.add(newChildrenNode);
				}
			}

			childrenNodes = newChildrenNodes;
		}

		return parents;
	}

	public List<SyntacticTreeNode> match(CompilationUnit unit, List<Token> tokens, int offset, String tree) {
		List<SyntacticTreeNode> parents = new ArrayList<>();
		parents.add(new SyntacticTreeNode(unit, this, offset, 0));
		List<SyntacticTreeNode> childNodes = new ArrayList<>();

		int localOffset = 0;
		while (true) {
			List<SyntacticTreeNode> matches = mToken.match(unit, tokens, offset + localOffset, tree);
			if (matches.isEmpty()) {
				break;
			}

			SyntacticTreeNode match = matches.stream()
					.max(Comparator.comparingInt(SyntacticTreeNode::length))
					.get();

			localOffset += match.length();

			childNodes.add(match);

			SyntacticTreeNode parent = new SyntacticTreeNode(unit, this, offset, localOffset);
			parent.addChildren(childNodes);
			parents.add(parent);
		}

		return parents;
	}

	@Override
	public LexicalTreeNode match(CompilationUnit unit, String input, int offset, String tree) {
		List<LexicalTreeNode> childNodes = new ArrayList<>();

		int localOffset = 0;
		while (true) {
			LexicalTreeNode match = mToken.match(unit, input, offset + localOffset, tree);
			if (match == null) {
				break;
			}

			localOffset += match.length();

			childNodes.add(match);
		}

		LexicalTreeNode parent = new LexicalTreeNode(unit, this, offset, offset + localOffset);
		parent.addChildren(childNodes);
		return parent;
	}

	@Override
	public String getName() {
		return "{" + mToken.toString() + "}";
	}
}
