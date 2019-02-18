package ws.codewash.parser.exception;

public class ParseException extends IllegalArgumentException {
	protected String mDescription;

	@Override
	public String getMessage() {
		return mDescription;
	}
}
