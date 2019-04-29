package ws.codewash.analyzer.smells.bloatedcode;

import ws.codewash.analyzer.reports.MemberReport;
import ws.codewash.analyzer.reports.Report;
import ws.codewash.analyzer.reports.Warning;
import ws.codewash.analyzer.smells.CodeSmell;
import ws.codewash.java.CWMember;
import ws.codewash.java.ParsedSourceTree;
import ws.codewash.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// TODO : Finish and test implementation

/**
 * Class used to manage the Long Parameter List Code Smell.
 */
public class LongParameterList extends CodeSmell {

	/**
	 * The name of the Code Smell. Used in reports.
	 */
	public static final String NAME = "LongParameterList";

	private LongParameterList.Config mConfig = ws.codewash.util.config.Config.get().configs.longParameterList;

	/**
	 * Constructor a LongParameterList with a specified {@link ParsedSourceTree} object.
	 *
	 * @param parsedSourceTree {@link ParsedSourceTree} object to use
	 */
	public LongParameterList(ParsedSourceTree parsedSourceTree) {
		super(parsedSourceTree);
	}

	/**
	 * Procedure for detecting Long Parameter Lists across the {@link ParsedSourceTree} object.
	 *
	 * @return A list of {@link ws.codewash.analyzer.reports.Report} which contain all of the problem methods.
	 */
	@Override
	public List<Report> run() {
		Log.i(NAME.toUpperCase(), "Running Long Parameter List check. Max Parameter = " + mConfig.maxParameters);

		List<Report> reports = new ArrayList<>();

		/*
			For each class in the parsed source tree get the methods of that class that match the predicate that their
			parameter list is greater than the maximum specified.
		*/
		super.getParsedSourceTree().getClasses().forEach((key, value) -> {
			List<CWMember> problemMethods = value.getMethods()
					.parallelStream()
					.filter(cwMethod -> cwMethod.getParameters().size() > mConfig.maxParameters)
					.collect(Collectors.toList());

			if (!problemMethods.isEmpty()) {
				problemMethods.forEach(problemMethod -> {
					reports.add(new MemberReport(NAME, value, problemMethod, Warning.CAUTION));
				});
				Log.d(NAME.toUpperCase(), "Created report for " + NAME + " " + value.getSimpleName());
			}
		});

		return reports;
	}

	public static class Config {
		public int maxParameters = 3;
	}

	/**
	 * Retrieves the name associated with each Code Smell.
	 *
	 * @return The name of the Code Smell.
	 */
	@Override
	public String getName() {
		return NAME;
	}
}
