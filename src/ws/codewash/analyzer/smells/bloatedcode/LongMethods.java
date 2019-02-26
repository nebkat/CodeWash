package ws.codewash.analyzer.smells.bloatedcode;

import ws.codewash.analyzer.Report;
import ws.codewash.analyzer.smells.CodeSmell;
import ws.codewash.java.CWClassOrInterface;
import ws.codewash.java.CWMethod;
import ws.codewash.parser.ParsedSourceTree;
import ws.codewash.util.Config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// TODO : Finish and test implementation
public class LongMethods extends CodeSmell {
	private static final String CONFIG_LENGTH = "MethodLength";

	public static final String NAME = "LongMethods";
	private final int METHOD_LENGTH;

	public LongMethods(ParsedSourceTree parsedSourceTree) {
		super(parsedSourceTree);
		METHOD_LENGTH = Config.get().LongMethodsConfig(CONFIG_LENGTH).intValue();
	}

	public String getName() {
		return NAME;
	}

	public Report run() {
		Report report = new Report(NAME, Report.Warning.ISSUE);

		Map<CWClassOrInterface, List<CWMethod>> longMethods = new HashMap<>();

		super.getParsedSourceTree().getClasses().forEach((key, value) -> {
			List<CWMethod> currentClassMethods = value.getMethods()
					.parallelStream()
					.filter(cwMethod -> cwMethod.getMethodLength() > METHOD_LENGTH)
					.collect(Collectors.toList());
			longMethods.put(value, currentClassMethods);
		});

		return report;
	}


}
