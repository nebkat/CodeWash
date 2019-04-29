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

//TODO : Finish implementation and test

/**
 * Class to manage the LongIDs Code Smell.
 */
public class LongIDs extends CodeSmell {


	/**
	 * The name of the Code Smell. Used in reports.
	 */
	public static final String NAME = "LongIDs";

	private LongIDs.Config mConfig = ws.codewash.util.config.Config.get().configs.longIDs;

	/**
	 * Constructs a LongIDs object with a specified {@link ParsedSourceTree} object.
	 *
	 * @param parsedSourceTree The {@link ParsedSourceTree} object to use.
	 */
	public LongIDs(ParsedSourceTree parsedSourceTree) {
		super(parsedSourceTree);
	}

	/**
	 * Procedure for detecting Long IDs across the {@link ParsedSourceTree} object.
	 *
	 * @return A list of {@link ws.codewash.analyzer.reports.Report} which contain all of the problem methods.
	 */
	@Override
	public List<Report> run() {
		Log.i(NAME.toUpperCase(), "Running Long IDs check. Max Characters = " + mConfig.maxCharacters);

		List<Report> reports = new ArrayList<>();

		/*
			For each class in the parsed source tree add all fields and methods whose ID exceeds the max length
			specified in the config.
		*/
		super.getParsedSourceTree().getClasses().forEach((key, value) -> {

			List<CWMember> longIDs;

			longIDs = value.getMethods().parallelStream()
					.filter(cwMethod -> cwMethod.getName().length() > mConfig.maxCharacters)
					.collect(Collectors.toList());

			longIDs.addAll(value.getFields().parallelStream()
					.filter(cwField -> !cwField.isFinal() && cwField.getName().length() > mConfig.maxCharacters)
					.collect(Collectors.toList()));


			if (!longIDs.isEmpty()) {
				Log.d(NAME.toUpperCase(), "Created report for " + NAME + " " + value.getSimpleName());
				reports.add(new MemberReport(NAME, value, longIDs, Warning.CAUTION));
			}
		});

		return reports;
	}

	public static class Config {
		private int maxCharacters = 25;
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
