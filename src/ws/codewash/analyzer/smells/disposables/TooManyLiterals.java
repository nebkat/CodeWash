package ws.codewash.analyzer.smells.disposables;

import ws.codewash.analyzer.reports.Report;
import ws.codewash.analyzer.smells.CodeSmell;
import ws.codewash.java.CompilationUnit;
import ws.codewash.java.ParsedSourceTree;
import ws.codewash.parser.input.Literal;
import ws.codewash.parser.input.StringLiteral;
import ws.codewash.parser.input.Token;
import ws.codewash.util.Arguments;
import ws.codewash.util.Log;
import ws.codewash.util.config.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class used to manage the TooManyLiteralCodeSmell
 */
public class TooManyLiterals extends CodeSmell {

	/**
	 * Name of the code smell, used for reports
	 */
	public static final String NAME = "TooManyLiterals";

	/**
	 * Config string used to retrieve value from config
	 */
	private static final String CONFIG_LITERAL_LENGTH = "LiteralLength";

	/**
	 * Config string used to retrieve value from config
	 */
	private static final String CONFIG_LITERAL_COUNT = "LiteralCount";

	/**
	 * Max length of string literal used that gets considered.
	 */
	private final int LITERAL_LENGTH = 4;

	/**
	 * Min number of the times literal has appeared to be considered.
	 */
	private final int LITERAL_COUNT;

	/**
	 * Whitelist of literals to ignore
	 */
	private final List<Object> BLACKLIST = Arrays.asList(0, 0.0, 0.0f, 1, 1.0, 1.0f, true, false, null, "", " ", "\n", "\t", "\r");

	/**
	 * Constructs a TooManyLiterals object with a {@link ws.codewash.java.ParsedSourceTree} object.
	 *
	 * @param parsedSourceTree The {@link ws.codewash.java.ParsedSourceTree} object to use.
	 */
	public TooManyLiterals(ParsedSourceTree parsedSourceTree) {
		super(parsedSourceTree);
//		LITERAL_LENGTH = Config.get().TooManyLiteralsConfig(CONFIG_LITERAL_LENGTH).intValue();
		LITERAL_COUNT = Config.get().TooManyLiteralsConfig(CONFIG_LITERAL_COUNT).intValue();
	}

	/**
	 * Defines the procedure to detect TooManyLiterals
	 *
	 * @return A list of {@link Report} which details problem Classes / Members
	 */
	@Override
	public List<Report> run() {
		List<Report> reports = new ArrayList<>();
		List<Literal> literals = getParsedSourceTree().getSources().stream()
				.map(CompilationUnit::getTokens)
				.flatMap(List::stream)
				.filter(Literal.class::isInstance)
				.map(Literal.class::cast)
				.collect(Collectors.toList());

		literals.removeIf(literal -> BLACKLIST.contains(literal.getValue()));
		literals.removeIf(literal -> literal instanceof StringLiteral
				&& ((StringLiteral) literal).getValue().length() <= LITERAL_LENGTH);

		Map<String, Integer> countMap = new HashMap<>();

		literals.forEach(token -> countMap.compute(token.getRawValue(), (k, v) -> (v == null) ? 1 : v + 1));

		List<String> tempList = new ArrayList<>();

		countMap.forEach((k, v) -> {
			if (v < LITERAL_LENGTH) {
				tempList.add(k);
			}
		});

		tempList.parallelStream().forEach(countMap::remove);

		if (Arguments.get().verbose()) {
			countMap.forEach((k, v) -> {
				if (v > LITERAL_COUNT) {
					Log.d(NAME, k + " -> " + v);
				}
			});
		}
//
//		if (!countMap.isEmpty()) {
//			reports.add(new LiteralReport(NAME, Warning.CAUTION));
//		}

		return reports;
	}

	/**
	 * Retrieves the name associated with Too Many Literals
	 *
	 * @return The name of the Code Smell.
	 */
	@Override
	public String getName() {
		return NAME;
	}
}
