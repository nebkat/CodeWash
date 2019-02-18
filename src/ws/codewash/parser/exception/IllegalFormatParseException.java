package ws.codewash.parser.exception;

import ws.codewash.parser.ParsedSourceTree;

public class IllegalFormatParseException extends SourceParseException {
	public IllegalFormatParseException(String description, ParsedSourceTree.Source source, int errorOffset) {
		super(description, source, errorOffset);
	}
}
