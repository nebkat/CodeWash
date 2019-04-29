package ws.codewash.analyzer.smells.bloatedcode;

import ws.codewash.analyzer.reports.MemberReport;
import ws.codewash.analyzer.reports.Report;
import ws.codewash.analyzer.reports.Warning;
import ws.codewash.analyzer.smells.CodeSmell;
import ws.codewash.java.CWMember;
import ws.codewash.java.ParsedSourceTree;
import ws.codewash.util.config.Config;
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
	 * The name of the Code Smell. Used in reports.
	 */
	public static final String NAME = "LongMethods";

	private LongMethods.Config mConfig = ws.codewash.util.config.Config.get().configs.longMethods;

	/**
	 * Constructs a LongMethods object with a specified {@link ParsedSourceTree} object.
	 *
	 * @param parsedSourceTree The {@link ParsedSourceTree} object to use.
	 */
	public LongMethods(ParsedSourceTree parsedSourceTree) {
		super(parsedSourceTree);
	}

	/**
	 * Procedure for detecting Long Methods across the {@link ParsedSourceTree} object.
	 *
	 * @return A list of {@link ws.codewash.analyzer.reports.Report} which contain all of the problem methods.
	 */
	public List<Report> run() {
		Log.i(NAME.toUpperCase(), "Running Long Methods check. Method Length = " + mConfig.methodLength);

		List<Report> reports = new ArrayList<>();

		/*
			For each class in the parsed source tree check the length of each method and if it's greater than the maximum
			declared in the config then add it to the report.
		*/
		super.getParsedSourceTree().getClasses().forEach((key, value) -> {
			List<CWMember> longMethods = value.getMethods()
					.parallelStream()
					.filter(cwMethod -> cwMethod.getMethodLength() > mConfig.methodLength)
					.collect(Collectors.toList());

			if (!longMethods.isEmpty()) {
				longMethods.forEach(cwMember -> reports.add(new MemberReport(NAME, value, cwMember, Warning.CAUTION)));
				Log.d(NAME.toUpperCase(), "Created report for " + NAME + " " + value.getSimpleName());
			}

		});

		return reports;
	}

	public static class Config {
		public int methodLength = 13;
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
