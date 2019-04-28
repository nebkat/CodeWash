package ws.codewash.parser.grammar;

import ws.codewash.util.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Grammar {
	private Path mPath;
	private Map<String, GrammarProduction> mProductions = new HashMap<>();

	private Grammar(Path path) {
		mPath = path;
	}

	public static Grammar parse(Path path) throws IOException {
		Grammar grammar = new Grammar(path);
		Parser parser = grammar.new Parser();
		parser.parse();
		return grammar;
	}

	public Path getPath() {
		return mPath;
	}

	private void addProduction(GrammarProduction production) {
		if (mProductions.containsKey(production.getSymbol())) {
			throw new IllegalArgumentException("Duplicate production " + production.getSymbol());
		}

		mProductions.put(production.getSymbol(), production);
	}

	public GrammarProduction getProduction(String name) {
		return mProductions.get(name);
	}

	private class Parser {
		private static final String TAG = "Grammar.Parser";

		private static final String TERMINAL_TOKEN_DELIMITER = "'";
		private static final String REGEX_TOKEN_DELIMITER = "`";
		private static final String REFERENCE_TOKEN_REGEX = "[a-zA-Z0-9]+";
		private static final String AND_NOT_EQUALS_MODIFIER = "&!=";
		private static final String OPTIONAL_OPEN_DELIMITER = "[";
		private static final String OPTIONAL_CLOSE_DELIMITER = "]";
		private static final String MULTIPLE_OPEN_DELIMITER = "{";
		private static final String MULTIPLE_CLOSE_DELIMITER = "}";
		private static final String NAME_OPEN_DELIMITER = "<";
		private static final String NAME_CLOSE_DELIMITER = ">";

		private final Pattern RULE_DECLARATION = Pattern.compile("(?<start>\\+?)(?<name>[a-zA-Z0-9]+)(?<greedy>\\*?):");

		private List<ReferenceGrammarToken> mReferenceTokens = new ArrayList<>();

		private void parse() throws IOException {
			List<String> lines = Files.readAllLines(mPath);
			Iterator<String> iterator = lines.iterator();
			int lineNumber = 0;
			while (iterator.hasNext()) {
				lineNumber++;
				String line = iterator.next();

				// Skip comments and empty lines
				if (line.startsWith("//") || line.isBlank()) continue;

				// Match new rules
				Matcher ruleMatcher = RULE_DECLARATION.matcher(line);
				if (!ruleMatcher.matches()) {
					// TODO: Handle
					throw new IllegalStateException("Invalid line " + line);
				}

				// Parse entire rule
				String ruleName = ruleMatcher.group("name");
				boolean start = !ruleMatcher.group("start").isEmpty();
				boolean greedy = !ruleMatcher.group("greedy").isEmpty();
				GrammarProduction production = new GrammarProduction(ruleName, start, greedy);

				while (iterator.hasNext()) {
					lineNumber++;
					line = iterator.next();

					// End of rule
					if (line.trim().isBlank()) break;

					GrammarTokenSet tokenSet;
					try {
						tokenSet = parseTokenSet(line.trim());
					} catch (Exception e) {
						throw new IllegalArgumentException("Error while parsing " + mPath.getFileName() + ":" + lineNumber, e);
					}

					// Add token set
					production.addAlternative(tokenSet);
				}

				// Add rule
				addProduction(production);
			}

			for (ReferenceGrammarToken referenceToken : mReferenceTokens) {
				referenceToken.resolveSymbol(Grammar.this);
			}

			for (GrammarProduction production : mProductions.values()) {
				if (!production.isReferenced()) {
					Log.v(TAG, "Production " + production.getSymbol() + " is not referenced by any other productions");
				}
			}
		}

		private GrammarToken parseToken(String token) {
			String tokenInner = token.length() > 2 ? token.substring(1, token.length() - 1) : "";

			GrammarToken t;
			if (token.matches(REFERENCE_TOKEN_REGEX)) {
				t = new ReferenceGrammarToken(token);
				mReferenceTokens.add((ReferenceGrammarToken) t);
			} else if (token.startsWith(TERMINAL_TOKEN_DELIMITER) && token.endsWith(TERMINAL_TOKEN_DELIMITER)) {
				t = new TerminalGrammarToken(tokenInner);
			} else if (token.startsWith(REGEX_TOKEN_DELIMITER) && token.endsWith(REGEX_TOKEN_DELIMITER)) {
				t = new RegexGrammarToken(tokenInner);
			} else {
				throw new IllegalStateException("Could not parse token " + token);
			}

			return t;
		}

		private GrammarTokenSet parseTokenSet(String line) {
			GrammarTokenSet tokenSet = new GrammarTokenSet();

			String[] tokens = line.split(" ");
			for (int i = 0; i < tokens.length; i++) {
				String token = tokens[i];

				boolean notCondition = false;
				if (token.startsWith(AND_NOT_EQUALS_MODIFIER)) {
					notCondition = true;

					token = token.substring(AND_NOT_EQUALS_MODIFIER.length());

					if (i != tokens.length - 1) {
						// TODO: Handle
						throw new IllegalStateException("&!= can only be found at end of token set");
					}
				}

				String tokenInner = token.length() > 2 ? token.substring(1, token.length() - 1) : "";

				if (token.startsWith(NAME_OPEN_DELIMITER) && token.endsWith(NAME_CLOSE_DELIMITER)) {
					if (i != 0) {
						// TODO: Handle
						throw new IllegalStateException("Token set name can only be defined at beginning of token set");
					}

					tokenSet.setName(tokenInner);
					continue;
				}

				GrammarToken t;
				if (token.startsWith(OPTIONAL_OPEN_DELIMITER) || token.startsWith(MULTIPLE_OPEN_DELIMITER)) {
					boolean optionalOrMultiple = token.startsWith(OPTIONAL_OPEN_DELIMITER);

					String openDelimiter = optionalOrMultiple ? OPTIONAL_OPEN_DELIMITER : MULTIPLE_OPEN_DELIMITER;
					String closeDelimiter = optionalOrMultiple ? OPTIONAL_CLOSE_DELIMITER : MULTIPLE_CLOSE_DELIMITER;

					if (token.replace(openDelimiter, "").length() ==
							token.replace(closeDelimiter, "").length()) {
						t = parseToken(tokenInner);
					} else {
						StringBuilder setBuilder = new StringBuilder();
						int delimiterDepth = 0;
						i--;
						do {
							token = tokens[++i];

							if (setBuilder.length() > 0) {
								setBuilder.append(" ");
							}
							setBuilder.append(token);

							delimiterDepth += token.length() - token.replace(openDelimiter, "").length();
							delimiterDepth -= token.length() - token.replace(closeDelimiter, "").length();
						} while (delimiterDepth > 0 && i < tokens.length - 1);

						if (delimiterDepth < 0) {
							throw new IllegalStateException("Too many closing delimiters found");
						} else if (delimiterDepth > 0) {
							throw new IllegalStateException("Closing delimiter not found");
						}

						String set = setBuilder.toString();
						set = set.substring(1, set.length() - 1);

						t = parseTokenSet(set);
					}

					t = optionalOrMultiple ? new OptionalGrammarToken(t) : new MultipleGrammarToken(t);
				} else {
					t = parseToken(token);
				}

				if (notCondition) {
					tokenSet.setNotCondition(t);
				} else {
					tokenSet.addToken(t);
				}
			}

			return tokenSet;
		}
	}
}
