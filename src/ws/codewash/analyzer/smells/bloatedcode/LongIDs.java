package ws.codewash.analyzer.smells.bloatedcode;

import ws.codewash.analyzer.reports.MemberReport;
import ws.codewash.analyzer.reports.Report;
import ws.codewash.analyzer.reports.Warning;
import ws.codewash.analyzer.smells.CodeSmell;
import ws.codewash.analyzer.smells.Smell;
import ws.codewash.java.CWClassOrInterface;
import ws.codewash.java.CWMember;
import ws.codewash.parser.ParsedSourceTree;
import ws.codewash.util.Config;
import ws.codewash.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//TODO : Finish implementation and test
public class LongIDs extends CodeSmell {

	public static final String CONFIG_CHARACTERS = "MaxCharacters";
	public static final String NAME = "LongIDs";

	private final int MAX_CHARACTERS;

	public LongIDs(ParsedSourceTree parsedSourceTree) {
		super(parsedSourceTree);
		MAX_CHARACTERS = Config.get().LongIDsConfig(CONFIG_CHARACTERS).intValue();
	}

	@Override
	public List<Report> run() {
		Log.i(NAME.toUpperCase(), "Running long ids check. Max Characters = " + MAX_CHARACTERS);

		List<Report> reports = new ArrayList<>();

		super.getParsedSourceTree().getClasses().forEach((key, value) -> {

			List<CWMember> longIDs;

			Log.d(NAME.toUpperCase(), "Number of fields in " + value.getSimpleName() + " : " + value.getFields().size());
			Log.d(NAME.toUpperCase(), "Number of methods in " + value.getSimpleName() + " : " + value.getMethods().size());

			longIDs = value.getMethods().parallelStream()
					.filter(cwMethod -> cwMethod.getName().length() > MAX_CHARACTERS)
					.collect(Collectors.toList());

			longIDs.addAll(value.getFields().parallelStream()
					.filter(cwField -> cwField.getName().length() > MAX_CHARACTERS)
					.collect(Collectors.toList()));

			Log.d(NAME.toUpperCase(), "Size of list for " + value.getSimpleName() + " : " + longIDs.size());

			if (!longIDs.isEmpty()) {
				reports.add(new MemberReport(Smell.LONG_IDS, value, longIDs, Warning.CAUTION));
			}
		});

		return reports;
	}

	@Override
	public String getName() {
		return NAME;
	}
}
