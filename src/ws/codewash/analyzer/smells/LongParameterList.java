package ws.codewash.analyzer.smells;

import ws.codewash.analyzer.Report;
import ws.codewash.java.CWClassOrInterface;
import ws.codewash.java.CWMethod;
import ws.codewash.parser.ParsedSourceTree;
import ws.codewash.util.Config;
import ws.codewash.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LongParameterList extends CodeSmell {
	private static final String CONFIG_LIST_LENGTH = "ParameterListLength";
	public final static String NAME = "LongParameterList";
	private final int LIST_LENGTH;

	public LongParameterList(ParsedSourceTree parsedSourceTree) {
		super(parsedSourceTree);
		LIST_LENGTH = (Integer) Config.get().LongParameterListConfig(CONFIG_LIST_LENGTH);
	}

	@Override
	public Report run() {

		Log.i(NAME.toUpperCase(), "Running long parameter list check");
		Report report = new Report(NAME, Report.Warning.ISSUE);

		Map<CWClassOrInterface, List<CWMethod>> longParamMethods = new HashMap<>();

		super.getParsedSourceTree().getClasses().forEach((key, value) -> {
			List<CWMethod> currentClassMethods = new ArrayList<>();
			value.getMethods().forEach(cwMethod -> {
				if (cwMethod.getParameters().size() > LIST_LENGTH) {
					currentClassMethods.add(cwMethod);
				}
			});
			longParamMethods.put(value, currentClassMethods);
		});

		return report;
	}

	@Override
	public String getName() {
		return NAME;
	}
}
