package ws.codewash.analyzer.smells;

import ws.codewash.analyzer.Report;
import ws.codewash.java.CWClassOrInterface;
import ws.codewash.parser.ParsedSourceTree;
import ws.codewash.util.Config;
import ws.codewash.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PrimitiveObsession extends CodeSmell {
	private static final String CONFIG_MIN_NUM = "MinimumNumberOfFields";
	private static final String CONFIG_ACCEPT_RATIO = "AcceptableRatio";
	public static final String NAME = "PrimitiveObsession";

	private final int MIN_NUM_FIELDS ;
	private final double ACCEPTABLE_RATIO;

	public PrimitiveObsession(ParsedSourceTree parsedSourceTree) {
		super(parsedSourceTree);
		MIN_NUM_FIELDS = Config.get().PrimitiveObsessionConfig(CONFIG_MIN_NUM).intValue();
		ACCEPTABLE_RATIO = Config.get().PrimitiveObsessionConfig(CONFIG_ACCEPT_RATIO).intValue();
	}

	@Override
	public Report run() {
		Log.i(NAME.toUpperCase(), "Running primitive obsession check");
		Report report = new Report(NAME, Report.Warning.ISSUE);

		Set<CWClassOrInterface> problemClasses = new HashSet<>();

		super.getParsedSourceTree().getClasses().forEach((key, value) -> {
			Map<CWClassOrInterface, Integer> totalFields = new HashMap<>();
			Map<CWClassOrInterface, Integer> totalPrimitives = new HashMap<>();

			value.getFields().parallelStream().forEach(cwField -> {

				// TODO: Add condition for static and final
				// TODO: Ensure only non local fields are included in both maps
				if (cwField.getType().isPrimitive()) {

					if (totalPrimitives.containsKey(value)) {
						totalPrimitives.put(value, totalPrimitives.get(value) + 1);
					} else {
						totalPrimitives.put(value, 1);
					}
				}

				if (totalFields.containsKey(value)) {
					totalFields.put(value, totalFields.get(value) + 1);
				} else {
					totalFields.put(value, 1);
				}
			});

			double ratio = (double) totalPrimitives.get(value) / totalFields.get(value);

			// If we have enough fields, check the ratio
			if (totalFields.get(value) > MIN_NUM_FIELDS) {
				if (ratio > ACCEPTABLE_RATIO) {
					problemClasses.add(value);
					Log.d(NAME.toUpperCase(), "Added " + value + " to problem classes");
				}
			} else {
				Log.d(NAME.toUpperCase(), "Not adding " + value + " to the problem classes\nRatio = " + ratio);
			}
		});
		return null;
	}

	@Override
	public String getName() {
		return NAME;
	}
}
