package ws.codewash.analyzer.smells;

import ws.codewash.analyzer.Report;

import ws.codewash.parser.ParsedSourceTree;

public class PrimitiveObsession extends CodeSmell {
	private static final String CONFIG_MIN_NUM = "MinimumNumberOfFields";
	private static final String CONFIG_ACCEPT_RATIO = "AcceptableRatio";
	public static final String NAME = "PrimitiveObsession";

	public PrimitiveObsession(ParsedSourceTree parsedSourceTree){
		super(parsedSourceTree);
	}

	@Override
	public Report run() {
		Report report = new Report(NAME, Report.Warning.ISSUE);

		return null;
	}

	@Override
	public String getName() {
		return NAME;
	}
}
