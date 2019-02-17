package ws.codewash.analyzer.smells;

import ws.codewash.parser.ParsedSourceTree;

public class RefusedBequest extends CodeSmell {

	public RefusedBequest(String name, ParsedSourceTree parsedSourceTree){
		super(name, parsedSourceTree);
	}

	public void run(){
		System.out.println("Checking for refused bequest");
	}
}
