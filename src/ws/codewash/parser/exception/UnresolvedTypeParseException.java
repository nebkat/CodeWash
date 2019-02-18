package ws.codewash.parser.exception;

import ws.codewash.parser.ParsedSourceTree;

public class UnresolvedTypeParseException extends SourceParseException {
	public UnresolvedTypeParseException(String description, ParsedSourceTree.Source source, int errorOffset) {
		super(description, source, errorOffset);
	}
}
