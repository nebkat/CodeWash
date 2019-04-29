package ws.codewash.analyzer.smells.disposables;

import ws.codewash.analyzer.reports.LiteralReport;
import ws.codewash.analyzer.reports.Report;
import ws.codewash.analyzer.reports.Warning;
import ws.codewash.analyzer.smells.CodeSmell;
import ws.codewash.java.CompilationUnit;
import ws.codewash.java.ParsedSourceTree;
import ws.codewash.parser.input.Literal;
import ws.codewash.parser.input.StringLiteral;
import ws.codewash.util.Arguments;
import ws.codewash.util.Log;
import ws.codewash.util.config.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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

	private Config mConfig = ws.codewash.util.config.Config.get().configs.tooManyLiterals;

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
	}

	/**
	 * Defines the procedure to detect TooManyLiterals
	 *
	 * @return A list of {@link Report} which details problem Classes / Members
	 */
	@Override
	public List<Report> run() {

		Log.i(NAME.toUpperCase(), "Running Too Many Literals check.\t| Params: Literal Count = " + mConfig.literalCount
				+ ", Literal Length = " + mConfig.literalLength);

		List<Report> reports = new ArrayList<>();
		List<Literal> literals = getParsedSourceTree().getSources().stream()
				.map(CompilationUnit::getTokens)
				.flatMap(List::stream)
				.filter(Literal.class::isInstance)
				.map(Literal.class::cast)
				.collect(Collectors.toList());

		literals.removeIf(literal -> BLACKLIST.contains(literal.getValue()));
		literals.removeIf(literal -> literal instanceof StringLiteral
				&& ((StringLiteral) literal).getValue().length() <= mConfig.literalLength);

		Map<String, Integer> countMap = new HashMap<>();

		literals.forEach(token -> countMap.compute(token.getRawValue(), (k, v) -> (v == null) ? 1 : v + 1));


		List<Literal> removalList = new ArrayList<>();

		countMap.forEach((k, v) -> {

			if (v < mConfig.literalLength) {
				for (Literal literal : literals) {
					if (literal.getRawValue().compareTo(k) == 0) {
						removalList.add(literal);
					}
				}
			}
		});

		literals.removeAll(removalList);
		
		if (!literals.isEmpty()) {
			literals.stream().forEach(literal -> reports.add(new LiteralReport(NAME, Warning.CAUTION, literal)));
		}

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

	public static class Config {
		private final int literalCount = 5;
		private final int literalLength = 4;
	}
}
