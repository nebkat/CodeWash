package ws.codewash.parser.exception;

import ws.codewash.parser.ParsedSourceTree;

public class RedeclarationParseException extends SourceParseException {
	public RedeclarationParseException(String description, ParsedSourceTree.Source source, int errorOffset) {
		super(description, source, errorOffset);
	}
}
