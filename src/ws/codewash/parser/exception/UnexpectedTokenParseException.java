package ws.codewash.parser.exception;

import ws.codewash.parser.ParsedSourceTree;

public class UnexpectedTokenParseException extends SourceParseException {
	public UnexpectedTokenParseException(String description, ParsedSourceTree.Source source, int errorOffset) {
		super(description, source, errorOffset);
	}
}
