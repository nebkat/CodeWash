package ws.codewash.analyzer.smells.disposables;

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

	private Config mConfig = ws.codewash.util.config.Config.get().configs.arrowheadIndentation;

	/**
	 * Constructs a CodeSmell Object with a {@link ParsedSourceTree} object
	 *
	 * @param parsedSourceTree The {@link ParsedSourceTree} to check for code smells
	 */
	public ArrowheadIndentation(ParsedSourceTree parsedSourceTree) {
		super(parsedSourceTree);
	}

	@Override
	public List<Report> run() {
		Log.i(NAME.toUpperCase(), "Running Arrowhead Indentations check.\t| Params: Max Depth = " + mConfig.maxDepth);

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

				if (!problemMethods.isEmpty()) {
					problemMethods.forEach(cwMember -> reports.add(new MemberReport(NAME, value, cwMember, Warning.WARNING)));
				}
			}
		});

		return reports;
	}

	/**
	 * Recursive function to detect arrowhead indentations of an If Statement.
	 *
	 * @param ifStatement the if statement to check
	 * @param depth       the current depth of the arrowhead indentation
	 * @return if the statement involves arrowhead indentation.
	 */
	private boolean detectArrowhead(CWIfStatement ifStatement, int depth) {
		if (depth > mConfig.maxDepth)
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

	public static class Config {
		private final int maxDepth = 4;
	}
}
