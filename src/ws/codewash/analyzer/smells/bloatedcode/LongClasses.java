package ws.codewash.analyzer.smells.bloatedcode;

import ws.codewash.analyzer.reports.ClassReport;
import ws.codewash.analyzer.reports.Report;
import ws.codewash.analyzer.reports.Warning;
import ws.codewash.analyzer.smells.CodeSmell;
import ws.codewash.java.CWMethod;
import ws.codewash.java.ParsedSourceTree;

import java.util.ArrayList;
import java.util.List;

public class LongClasses extends CodeSmell {

	public final static String NAME = "LongClasses";

	private Config mConfig = ws.codewash.util.config.Config.get().configs.longClasses;

	public LongClasses(ParsedSourceTree parsedSourceTree) {
		super(parsedSourceTree);

	}

	// TODO : Add in count for the actual lines of the file - empty lines
	@Override
	public List<Report> run() {

		List<Report> reports = new ArrayList<>();

		super.getParsedSourceTree().getClasses().forEach((key, value) -> {
			int totalFields = (int) value.getFields().parallelStream().filter(cwField -> !cwField.isFinal()).count();

			int totalMethods = value.getMethods().size();

			int totalContructorsLength = 0;

//			for (CWConstructor cwConstructor : value.getConstructors()) {
//				totalContructorsLength += cwConstructor.
//			}

			int totalMethodLength = 0;
			for (CWMethod cwMethod : value.getMethods()) {
				totalMethodLength += cwMethod.getMethodLength();
			}

			if (totalFields > mConfig.maxFields && totalMethods > mConfig.maxMethods
					|| totalFields > mConfig.maxFields && totalMethodLength + totalContructorsLength > mConfig.maxMethodLength) {
				reports.add(new ClassReport(NAME, value, Warning.WARNING));
			} else if (totalMethodLength + totalContructorsLength > mConfig.maxMethodLength) {
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


	public static class Config {
		private final int maxFields = 15;
		private final int maxMethods = 15;
		private final int maxMethodLength = 300;
	}

}
