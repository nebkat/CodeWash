package ws.codewash.parser.grammar;

import ws.codewash.parser.tree.SyntacticTreeNode;
import ws.codewash.java.CompilationUnit;
import ws.codewash.parser.Token;
import ws.codewash.parser.tree.LexicalTreeNode;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexGrammarToken extends GrammarToken {
	private String mRegex;
	private Pattern mPattern;

	RegexGrammarToken(String regex) {
		mRegex = regex;
		mPattern = Pattern.compile(mRegex);
	}

	@Override
	public List<SyntacticTreeNode> match(CompilationUnit unit, List<Token> tokens, int offset, String tree) {
		if (tokens.size() <= offset) {
			return Collections.emptyList();
		}

		if (mPattern.matcher(tokens.get(offset).getValue()).matches()) {
			return Collections.singletonList(new SyntacticTreeNode(unit, this, offset, 1));
		}

		return Collections.emptyList();
	}

	@Override
	public LexicalTreeNode match(CompilationUnit unit, String input, int offset, String tree) {
		Matcher matcher = mPattern.matcher(input);
		matcher.region(offset, input.length());
		if (matcher.lookingAt()) {
			return new LexicalTreeNode(unit, this, offset, offset + matcher.group().length());
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
		return "`" + mRegex + "`";
	}
}
