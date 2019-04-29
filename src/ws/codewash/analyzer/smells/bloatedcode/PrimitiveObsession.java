package ws.codewash.analyzer.smells.bloatedcode;

import ws.codewash.analyzer.reports.ClassReport;
import ws.codewash.analyzer.reports.Report;
import ws.codewash.analyzer.reports.Warning;
import ws.codewash.analyzer.smells.CodeSmell;
import ws.codewash.java.ParsedSourceTree;
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

	private PrimitiveObsession.Config mConfig = ws.codewash.util.config.Config.get().configs.primitiveObsession;
	/**
	 * Constructs a Primitive Obsession object with a {@link ws.codewash.java.ParsedSourceTree} object
	 *
	 * @param parsedSourceTree The {@link ws.codewash.java.ParsedSourceTree} to check for Primitive Obsession.
	 */
	public PrimitiveObsession(ParsedSourceTree parsedSourceTree) {
		super(parsedSourceTree);
	}

	/**
	 * Procedure for detecting Primitive Obsession across the {@link ws.codewash.java.ParsedSourceTree} object.
	 *
	 * @return A list of {@link ws.codewash.analyzer.reports.Report} which contains all of the problem classes.
	 */
	@Override
	public List<Report> run() {
		Log.i(NAME.toUpperCase(), "Running Primitive Obsession check.\n\tMin Fields = " + mConfig.minReqFields
				+ "\n\tAcceptable Ratio = " + mConfig.acceptableRatio);

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
			if (totalFields > 0) {
				double ratio = (double) totalPrimitives / totalFields;


				if (totalFields > mConfig.minReqFields && ratio > mConfig.acceptableRatio) {
					reports.add(new ClassReport(NAME,  Warning.CAUTION, value));
					Log.d(NAME.toUpperCase(), "Created report for " + NAME + " " + value.getSimpleName());
				} else {
					Log.d(NAME.toUpperCase(), "Not creating report for " + value.getSimpleName() + " Num primitives = " + totalPrimitives);
				}
			}
		});

		return reports;
	}

	public static class Config {
		public int minReqFields = 5;
		public double acceptableRatio = 0.5;
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
