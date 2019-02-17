package ws.codewash.analyzer.smells;

import ws.codewash.parser.ParsedSourceTree;

public class ExcessiveCommenting extends CodeSmell {

	public ExcessiveCommenting(String name, ParsedSourceTree parsedSourceTree){
		super(name, parsedSourceTree);

	}

	public void run(){
		System.out.println("Checking for excessive use of comments");
	}
}
