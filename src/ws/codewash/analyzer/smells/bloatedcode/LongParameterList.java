package ws.codewash.analyzer.smells.bloatedcode;

import ws.codewash.analyzer.reports.MemberReport;
import ws.codewash.analyzer.reports.Report;
import ws.codewash.analyzer.reports.Warning;
import ws.codewash.analyzer.smells.CodeSmell;
import ws.codewash.java.CWMember;
import ws.codewash.java.ParsedSourceTree;
import ws.codewash.util.Config;
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

	/**
	 * String used to retrieve the max Parameter List length from the config.
	 */
	private static final String CONFIG_PARAMETER_LIST_LENGTH = "ParameterListLength";

	/**
	 * Maximum number of parameters a method can have before being considered a Code Smell
	 */
	private final int MAX_PARAMETER_LENGTH;

	/**
	 * Constructor a LongParameterList with a specified {@link ParsedSourceTree} object.
	 *
	 * @param parsedSourceTree {@link ParsedSourceTree} object to use
	 */
	public LongParameterList(ParsedSourceTree parsedSourceTree) {
		super(parsedSourceTree);
		MAX_PARAMETER_LENGTH = Config.get().LongParameterListConfig(CONFIG_PARAMETER_LIST_LENGTH).intValue();
	}

	/**
	 * Procedure for detecting Long Parameter Lists across the {@link ParsedSourceTree} object.
	 *
	 * @return A list of {@link ws.codewash.analyzer.reports.Report} which contain all of the problem methods.
	 */
	@Override
	public List<Report> run() {
		Log.i(NAME.toUpperCase(), "Running Long Parameter List check. Max Parameter = " + MAX_PARAMETER_LENGTH);

		List<Report> reports = new ArrayList<>();

		/*
			For each class in the parsed source tree get the methods of that class that match the predicate that their
			parameter list is greater than the maximum specified.
		*/
		super.getParsedSourceTree().getClasses().forEach((key, value) -> {
			List<CWMember> problemMethods = value.getMethods()
					.parallelStream()
					.filter(cwMethod -> cwMethod.getParameters().size() > MAX_PARAMETER_LENGTH)
					.collect(Collectors.toList());

			if (!problemMethods.isEmpty()) {
				reports.add(new MemberReport(NAME, value, problemMethods, Warning.CAUTION));
			}
		});

		return reports;
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
