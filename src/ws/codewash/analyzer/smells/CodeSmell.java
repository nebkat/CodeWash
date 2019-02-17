package ws.codewash.analyzer.smells;

import ws.codewash.parser.ParsedSourceTree;

public abstract class CodeSmell {

	private String mName;
	private ParsedSourceTree mParsedSourceTree;

	CodeSmell(String name, ParsedSourceTree parsedSourceTree){
		mName = name;
		mParsedSourceTree = parsedSourceTree;
	}

	public abstract void run();

	protected String getName() {
		return mName;
	}

	protected ParsedSourceTree getParsedSourceTree() {
		return mParsedSourceTree;
	}
}
