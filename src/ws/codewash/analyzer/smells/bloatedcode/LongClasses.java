package ws.codewash.analyzer.smells.bloatedcode;

import ws.codewash.analyzer.reports.ClassReport;
import ws.codewash.analyzer.reports.Report;
import ws.codewash.analyzer.reports.Warning;
import ws.codewash.analyzer.smells.CodeSmell;
import ws.codewash.java.CWMethod;
import ws.codewash.java.ParsedSourceTree;
import ws.codewash.util.config.Config;

import java.util.ArrayList;
import java.util.List;

public class LongClasses extends CodeSmell {

	public final static String NAME = "LongClasses";
	private final String CONFIG_MAX_CLASS_LENGTH = "MaxClassLength";
	private final String CONFIG_MAX_NUM_FIELDS = "MaxFields";
	private final String CONFIG_MAX_NUM_METHODS = "MaxMethods";

	private final int MAX_LENGTH;
	private final int MAX_METHODS;
	private final int MAX_FIELDS;

	public LongClasses(ParsedSourceTree parsedSourceTree) {
		super(parsedSourceTree);
		MAX_LENGTH = Config.get().LongClassConfig(CONFIG_MAX_CLASS_LENGTH).intValue();
		MAX_METHODS = Config.get().LongClassConfig(CONFIG_MAX_NUM_METHODS).intValue();
		MAX_FIELDS = Config.get().LongClassConfig(CONFIG_MAX_NUM_FIELDS).intValue();
	}

	// TODO : Add in count for the actual lines of the file - empty lines
	@Override
	public List<Report> run() {

		List<Report> reports = new ArrayList<>();

		super.getParsedSourceTree().getClasses().forEach((key, value) -> {
			int totalFields = (int)value.getFields().parallelStream().filter(cwField -> !cwField.isFinal()).count();

			int totalMethods = value.getMethods().size();

			int totalContructorsLength = 0;

//			for (CWConstructor cwConstructor : value.getConstructors()) {
//				totalContructorsLength += cwConstructor.
//			}

			int totalMethodLength = 0;
			for (CWMethod cwMethod : value.getMethods()) {
				totalMethodLength += cwMethod.getMethodLength();
			}

			if (totalFields > MAX_FIELDS && totalMethods > MAX_METHODS && totalMethodLength + totalContructorsLength > MAX_LENGTH
				|| totalFields > MAX_FIELDS && totalMethods > MAX_METHODS || totalFields > MAX_FIELDS && totalMethodLength + totalContructorsLength > MAX_LENGTH
				|| totalFields > MAX_METHODS && totalMethodLength + totalContructorsLength > MAX_LENGTH) {
				reports.add(new ClassReport(NAME, value, Warning.WARNING));
			}
			else if (totalMethodLength + totalContructorsLength > MAX_LENGTH) {
				reports.add(new ClassReport(NAME, value, Warning.CAUTION));
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
