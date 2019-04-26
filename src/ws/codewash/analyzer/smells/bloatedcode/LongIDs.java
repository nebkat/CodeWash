package ws.codewash.analyzer.smells.bloatedcode;

import ws.codewash.analyzer.reports.MemberReport;
import ws.codewash.analyzer.reports.Report;
import ws.codewash.analyzer.reports.Warning;
import ws.codewash.analyzer.smells.CodeSmell;
import ws.codewash.java.CWMember;
import ws.codewash.parser.ParsedSourceTree;
import ws.codewash.util.Config;
import ws.codewash.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//TODO : Finish implementation and test

/**
 * Class to manage the LongIDs Code Smell.
 */
public class LongIDs extends CodeSmell {


	/**
	 * The name of the Code Smell. Used in reports.
	 */
	public static final String NAME = "LongIDs";

	/**
	 * String used to retrieve the maximum number of characters from the config.
	 */
	public static final String CONFIG_CHARACTERS = "MaxCharacters";

	/**
	 * The maximum number of characters in an ID before it is considered a Code Smell. Retrieved from the config.
	 */
	private final int MAX_CHARACTERS;

	/**
	 * Constructs a LongIDs object with a specified {@link ws.codewash.parser.ParsedSourceTree} object.
	 *
	 * @param parsedSourceTree The {@link ws.codewash.parser.ParsedSourceTree} object to use.
	 */
	public LongIDs(ParsedSourceTree parsedSourceTree) {
		super(parsedSourceTree);
		MAX_CHARACTERS = Config.get().LongIDsConfig(CONFIG_CHARACTERS).intValue();
	}

	/**
	 * Procedure for detecting Long IDs across the {@link ws.codewash.parser.ParsedSourceTree} object.
	 *
	 * @return A list of {@link ws.codewash.analyzer.reports.Report} which contain all of the problem methods.
	 */
	@Override
	public List<Report> run() {
		Log.i(NAME.toUpperCase(), "Running Long IDs check. Max Characters = " + MAX_CHARACTERS);

		List<Report> reports = new ArrayList<>();

		/*
			For each class in the parsed source tree add all fields and methods whose ID exceeds the max length
			specified in the config.
		*/
		super.getParsedSourceTree().getClasses().forEach((key, value) -> {

			List<CWMember> longIDs;

			longIDs = value.getMethods().parallelStream()
					.filter(cwMethod -> cwMethod.getName().length() > MAX_CHARACTERS)
					.collect(Collectors.toList());

			longIDs.addAll(value.getFields().parallelStream()
					.filter(cwField -> cwField.getName().length() > MAX_CHARACTERS)
					.collect(Collectors.toList()));

			Log.d(NAME.toUpperCase(), "Size of list for " + value.getSimpleName() + " : " + longIDs.size());

			if (!longIDs.isEmpty()) {
				reports.add(new MemberReport(NAME, value, longIDs, Warning.CAUTION));
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
