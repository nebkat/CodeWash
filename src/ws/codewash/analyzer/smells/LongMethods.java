package ws.codewash.analyzer.smells;

import ws.codewash.analyzer.Report;
import ws.codewash.java.CWClassOrInterface;
import ws.codewash.java.CWMethod;
import ws.codewash.parser.ParsedSourceTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LongMethods extends CodeSmell {

	public final static String NAME = "LongMethods";
	private final int METHOD_LENGTH = 10;

	public LongMethods(ParsedSourceTree parsedSourceTree) {
		super(parsedSourceTree);
	}

	public String getName() {
		return NAME;
	}

	public Report run() {
		Report report = new Report(NAME, Report.Warning.ISSUE);

		// Stores methods with long method bodies and the class they are contained in
		Map<CWClassOrInterface, List<CWMethod>> longMethods = new HashMap<>();

		super.getParsedSourceTree().getClasses().forEach((key, value) -> {
			List<CWMethod> currentClassMethods = new ArrayList<>();
			value.getMethods().forEach(cwMethod -> {
				if (cwMethod.getMethodLength() > METHOD_LENGTH) {
					currentClassMethods.add(cwMethod);
				}
			});
			longMethods.put(value, currentClassMethods);
		});

		return report;
	}


}
