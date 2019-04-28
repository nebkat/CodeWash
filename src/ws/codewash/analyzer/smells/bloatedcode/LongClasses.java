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

public class LongClasses extends CodeSmell {

	public final static String NAME = "LongClasses";
	private final String CONFIG_MAX_CLASS_LENGTH = "MaxClassLength";
	private final String CONFIG_MID_CLASS_LENGTH = "MidClassLength";
	private final int MAX_LENGTH, MID_LENGTH;

	public LongClasses(ParsedSourceTree parsedSourceTree) {
		super(parsedSourceTree);
		MAX_LENGTH = Config.get().LongClassConfig(CONFIG_MAX_CLASS_LENGTH).intValue();
		MID_LENGTH = Config.get().LongClassConfig(CONFIG_MID_CLASS_LENGTH).intValue();
	}

	// TODO : Add in count for the actual lines of the file - empty lines
	@Override
	public List<Report> run() {

		List<Report> reports = new ArrayList<>();

		super.getParsedSourceTree().getClasses().forEach((key, value) -> {
			int totalLength = (int) value.getMethods().parallelStream().count();
			totalLength += (int) value.getFields().parallelStream().count();

			if (totalLength <= MAX_LENGTH && totalLength >= MID_LENGTH) {
				Log.d(NAME.toUpperCase(), "Created report for " + NAME + " " + value.getSimpleName());
				reports.add(new ClassReport(NAME, value, Warning.CAUTION));
			} else if (totalLength > MAX_LENGTH) {
				reports.add(new ClassReport(NAME, value, Warning.ISSUE));
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
