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
 * Class which manages the LongMethods Code Smell.
 */
public class LongMethods extends CodeSmell {

	/**
	 * String used to retrieve the max method length from the config.
	 */
	private static final String CONFIG_LENGTH = "MethodLength";

	/**
	 * The name of the Code Smell. Used in reports.
	 */
	public static final String NAME = "LongMethods";

	/**
	 * Maximum method length before it is considered a Code Smell, retrieved from config.
	 */
	private final int METHOD_LENGTH;

	/**
	 * Constructs a LongMethods object with a specified {@link ParsedSourceTree} object.
	 *
	 * @param parsedSourceTree The {@link ParsedSourceTree} object to use.
	 */
	public LongMethods(ParsedSourceTree parsedSourceTree) {
		super(parsedSourceTree);
		METHOD_LENGTH = Config.get().LongMethodsConfig(CONFIG_LENGTH).intValue();
	}

	/**
	 * Procedure for detecting Long Methods across the {@link ParsedSourceTree} object.
	 *
	 * @return A list of {@link ws.codewash.analyzer.reports.Report} which contain all of the problem methods.
	 */
	public List<Report> run() {
		Log.i(NAME.toUpperCase(), "Running Long Methods check. Method Length = " + METHOD_LENGTH);

		List<Report> reports = new ArrayList<>();

		/*
			For each class in the parsed source tree check the length of each method and if it's greater than the maximum
			declared in the config then add it to the report.
		*/
		super.getParsedSourceTree().getClasses().forEach((key, value) -> {
			List<CWMember> longMethods = value.getMethods()
					.parallelStream()
					.filter(cwMethod -> cwMethod.getMethodLength() > METHOD_LENGTH)
					.collect(Collectors.toList());

			reports.add(new MemberReport(NAME, value, longMethods, Warning.CAUTION));
		});

		return reports;
	}

	/**
	 * Retrieves the name associated with each Code Smell.
	 *
	 * @return The name of the Code Smell.
	 */
	public String getName() {
		return NAME;
	}
}
