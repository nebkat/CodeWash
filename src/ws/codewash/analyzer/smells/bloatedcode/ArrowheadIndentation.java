package ws.codewash.analyzer.smells.bloatedcode;

import ws.codewash.analyzer.reports.MemberReport;
import ws.codewash.analyzer.reports.Report;
import ws.codewash.analyzer.reports.Warning;
import ws.codewash.analyzer.smells.CodeSmell;
import ws.codewash.java.*;
import ws.codewash.java.statement.CWIfStatement;
import ws.codewash.util.Log;
import ws.codewash.util.config.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ArrowheadIndentation extends CodeSmell {
	/**
	 * The name of the Code Smell. Used in reports.
	 */
	public static final String NAME = "ArrowheadIndentation";

	/**
	 * String used to retrieve the maximum number of characters from the config.
	 */
	private static final String CONFIG_DEPTH = "MaxDepth";

	private final int MAX_DEPTH;

	/**
	 * Constructs a CodeSmell Object with a {@link ParsedSourceTree} object
	 *
	 * @param parsedSourceTree The {@link ParsedSourceTree} to check for code smells
	 */
	public ArrowheadIndentation(ParsedSourceTree parsedSourceTree) {
		super(parsedSourceTree);
		MAX_DEPTH = Config.get().ArrowheadConfig(CONFIG_DEPTH).intValue();
	}

	@Override
	public List<Report> run() {
		Log.i(NAME.toUpperCase(), "Running Arrowhead Indentation:");

		List<Report> reports = new ArrayList<>();

		super.getParsedSourceTree().getClasses().forEach((key, value) -> {

			if (value instanceof CWClass) {
				List<CWMember> problemMethods = value.getMethods().stream()
						.filter(cwMethod -> {

							if (cwMethod.getBlock() != null)
								return cwMethod.getBlock().getClosestDescendants(CWIfStatement.class)
										.stream()
										.anyMatch(s -> {
											if (s != null) {
												return detectArrowhead(s, 0);
											}
											return false;
										});
							return false;

						}).collect(Collectors.toList());

				if (!problemMethods.isEmpty())
					reports.add(new MemberReport(NAME, value, problemMethods, Warning.WARNING));
			}
		});

		return reports;
	}

	/**
	 * Recursive function to detect arrowhead indentations of an If Statement.
	 *
	 * @param ifStatement the if statement to check
	 * @param depth the current depth of the arrowhead indentation
	 * @return if the statement involves arrowhead indentation.
	 */
	private boolean detectArrowhead(CWIfStatement ifStatement, int depth) {
		if (depth > MAX_DEPTH)
			return true;

		// Checking the block of the if statement is arrowhead
		if (ifStatement.getThenStatement() != null) {
			List<CWIfStatement> descendants = ifStatement.getThenStatement().getClosestDescendants(CWIfStatement.class);
			if (descendants.size() > 0) {
				//	single if statement detection
				if (descendants.size() == 1 && ifStatement.getElseStatement() == null) {
					if (detectArrowhead(descendants.get(0), depth + 1))
						return true;
				} else {
					//	restarting check for more than one if statement
					for (CWIfStatement d : descendants) {
						if (detectArrowhead(d, 0))
							return true;
					}
				}
			}
		}

		//	Checking the else statement streams
		if (ifStatement.getElseStatement() != null) {
			List<CWIfStatement> descendants = ifStatement.getElseStatement().getClosestDescendants(CWIfStatement.class);
			if (descendants.size() > 0) {
				for (CWIfStatement d : descendants) {
					//	restarting in else block
					if (detectArrowhead(d, 0))
						return true;
				}
			}
		}

		return false;
	}

	@Override
	public String getName() {
		return NAME;
	}
}
