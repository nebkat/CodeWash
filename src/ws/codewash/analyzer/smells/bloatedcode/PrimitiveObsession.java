package ws.codewash.analyzer.smells.bloatedcode;

import ws.codewash.analyzer.reports.ClassReport;
import ws.codewash.analyzer.reports.Report;
import ws.codewash.analyzer.reports.Warning;
import ws.codewash.analyzer.smells.CodeSmell;
import ws.codewash.java.ParsedSourceTree;
import ws.codewash.util.Config;
import ws.codewash.util.Log;

import java.util.ArrayList;
import java.util.List;

// TODO : Finish and test implementation

/**
 * Class used to manage the Primitive Obsession Code Smell.
 */
public class PrimitiveObsession extends CodeSmell {

	/**
	 * The name of the Code Smell, used in the reports.
	 */
	public static final String NAME = "PrimitiveObsession";

	/**
	 * String used to retrieve the minimum number of fields from the Config.
	 */
	private static final String CONFIG_MIN_NUM = "MinimumNumberOfFields";

	/**
	 * String used to retrieve the acceptable ratio of primitive fields to total fields.
	 */
	private static final String CONFIG_ACCEPT_RATIO = "AcceptableRatio";

	/**
	 * Minimum number of fields required to test for Primitive Obsession, retrieved from the config
	 */
	private final int MIN_NUM_FIELDS;

	/**
	 * Acceptable ratio of primitive fields to total fields, retrieved from the config
	 */
	private final double ACCEPTABLE_RATIO;

	/**
	 * Constructs a Primitive Obsession object with a {@link ParsedSourceTree} object
	 *
	 * @param parsedSourceTree The {@link ParsedSourceTree} to check for Primitive Obsession.
	 */
	public PrimitiveObsession(ParsedSourceTree parsedSourceTree) {
		super(parsedSourceTree);
		MIN_NUM_FIELDS = Config.get().PrimitiveObsessionConfig(CONFIG_MIN_NUM).intValue();
		ACCEPTABLE_RATIO = Config.get().PrimitiveObsessionConfig(CONFIG_ACCEPT_RATIO).intValue();
	}

	/**
	 * Procedure for detecting Primitive Obsession across the {@link ParsedSourceTree} object.
	 *
	 * @return A list of {@link ws.codewash.analyzer.reports.Report} which contains all of the problem classes.
	 */
	@Override
	public List<Report> run() {
		Log.i(NAME.toUpperCase(), "Running Primitive Obsession check.\n\tMin Fields = " + MIN_NUM_FIELDS + "\n\tAcceptable Ratio = " + ACCEPTABLE_RATIO);

		List<Report> reports = new ArrayList<>();

		/*
			For each class in the parsed source tree count the number of fields which are primitive and the total number
			of fields. Then compare the ratio of the two to the acceptable ratio.

			If there isn't enough fields then don't check the ratio.
		*/
		super.getParsedSourceTree().getClasses().forEach((key, value) -> {
			int totalPrimitives = (int) value.getFields()
					.parallelStream()
					.filter(cwField -> cwField.getType().isPrimitive())
					.count();
			int totalFields = value.getFields().size();
			double ratio = (double) totalPrimitives / totalFields;

			if (totalFields > MIN_NUM_FIELDS && ratio > ACCEPTABLE_RATIO) {
				reports.add(new ClassReport(NAME, value, Warning.CAUTION));
				Log.d(NAME.toUpperCase(), "Created report for " + value.getSimpleName());
			} else {
				Log.d(NAME.toUpperCase(), "Not creating report for " + value.getSimpleName());
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
