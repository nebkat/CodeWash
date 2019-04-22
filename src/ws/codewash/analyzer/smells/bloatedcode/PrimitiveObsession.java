package ws.codewash.analyzer.smells.bloatedcode;

import ws.codewash.analyzer.reports.ClassReport;
import ws.codewash.analyzer.reports.Report;
import ws.codewash.analyzer.reports.Warning;
import ws.codewash.analyzer.smells.CodeSmell;
import ws.codewash.analyzer.smells.Smell;
import ws.codewash.java.CWClassOrInterface;
import ws.codewash.java.CWField;
import ws.codewash.parser.ParsedSourceTree;
import ws.codewash.util.Config;
import ws.codewash.util.Log;

import java.util.*;

// TODO : Finish and test implementation
public class PrimitiveObsession extends CodeSmell {
	private static final String CONFIG_MIN_NUM = "MinimumNumberOfFields";
	private static final String CONFIG_ACCEPT_RATIO = "AcceptableRatio";
	public static final String NAME = "PrimitiveObsession";

	private final int MIN_NUM_FIELDS;
	private final double ACCEPTABLE_RATIO;

	public PrimitiveObsession(ParsedSourceTree parsedSourceTree) {
		super(parsedSourceTree);
		MIN_NUM_FIELDS = Config.get().PrimitiveObsessionConfig(CONFIG_MIN_NUM).intValue();
		ACCEPTABLE_RATIO = Config.get().PrimitiveObsessionConfig(CONFIG_ACCEPT_RATIO).intValue();
	}

	@Override
	public List<Report> run() {
		Log.i(NAME.toUpperCase(), "Running primitive obsession check");

		List<Report> reports = new ArrayList<>();

		super.getParsedSourceTree().getClasses().forEach((key, value) -> {
			int totalPrimitives = (int)value.getFields()
					.parallelStream()
					.filter(cwField -> cwField.getType().isPrimitive())
					.count();
			int totalFields = value.getFields().size();
			double ratio = (double) totalPrimitives / totalFields;

			if (totalFields > MIN_NUM_FIELDS && ratio > ACCEPTABLE_RATIO) {
				reports.add(new ClassReport(Smell.PRIMITIVE_OBSESSION, value, Warning.CAUTION));
				Log.d(NAME.toUpperCase(), "Created report for " + value.getSimpleName());
			} else {
				Log.d(NAME.toUpperCase(), "Not creating report for " + value.getSimpleName());
			}
		});

		return reports;
	}

	@Override
	public String getName() {
		return NAME;
	}
}
