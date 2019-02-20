package ws.codewash.analyzer.smells;

import ws.codewash.analyzer.Report;
import ws.codewash.parser.ParsedSourceTree;

public abstract class CodeSmell {

	private ParsedSourceTree mParsedSourceTree;

	CodeSmell(ParsedSourceTree parsedSourceTree){
		mParsedSourceTree = parsedSourceTree;
	}

	public abstract Report run();

	public abstract String getName();

	ParsedSourceTree getParsedSourceTree() {
		return mParsedSourceTree;
	}
}

