package ws.codewash.analyzer.smells;

import ws.codewash.analyzer.reports.Report;
import ws.codewash.parser.ParsedSourceTree;

import java.util.List;

public abstract class CodeSmell {

	private ParsedSourceTree mParsedSourceTree;

	protected CodeSmell(ParsedSourceTree parsedSourceTree) {
		mParsedSourceTree = parsedSourceTree;
	}

	public abstract List<Report> run();

	public abstract String getName();

	protected ParsedSourceTree getParsedSourceTree() {
		return mParsedSourceTree;
	}
}

