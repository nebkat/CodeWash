package ws.codewash.analyzer.smells.bloatedcode;

import ws.codewash.analyzer.reports.MemberReport;
import ws.codewash.analyzer.reports.Report;
import ws.codewash.analyzer.reports.Warning;
import ws.codewash.analyzer.smells.CodeSmell;
import ws.codewash.analyzer.smells.Smell;
import ws.codewash.java.CWMember;
import ws.codewash.parser.ParsedSourceTree;
import ws.codewash.util.Config;
import ws.codewash.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// TODO : Finish and test implementation
public class LongParameterList extends CodeSmell {

	private static final String CONFIG_LIST_LENGTH = "ParameterListLength";
	public static final String NAME = "LongParameterList";

	private final int LIST_LENGTH;

	public LongParameterList(ParsedSourceTree parsedSourceTree) {
		super(parsedSourceTree);
		LIST_LENGTH = Config.get().LongParameterListConfig(CONFIG_LIST_LENGTH).intValue();
	}

	@Override
	public List<Report> run() {

		Log.i(NAME.toUpperCase(), "Running long parameter list check");
		List<Report> reports = new ArrayList<>();

		super.getParsedSourceTree().getClasses().forEach((key, value) -> {
			List<CWMember> problemMethods = value.getMethods()
					.parallelStream()
					.filter(cwMethod -> cwMethod.getParameters().size() > LIST_LENGTH)
					.collect(Collectors.toList());

			if (!problemMethods.isEmpty()) {
				reports.add(new MemberReport(Smell.LONG_PARAMETER_LISTS, value, problemMethods, Warning.CAUTION));
			}
		});

		return reports;
	}

	@Override
	public String getName() {
		return NAME;
	}
}
