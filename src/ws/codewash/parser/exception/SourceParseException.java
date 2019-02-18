package ws.codewash.parser.exception;

import ws.codewash.parser.ParsedSourceTree;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class SourceParseException extends ParseException {
	private static final Pattern NON_WHITESPACE_PATTERN = Pattern.compile("[^\\s]");

	private String mDescription;
	private ParsedSourceTree.Source mSource;
	private int mErrorOffset;

	public SourceParseException(String description, ParsedSourceTree.Source source, int errorOffset) {
		mDescription = description;
		mSource = source;

		// Comments are replaced with whitespace, so move to first non whitespace character
		Matcher nonWhitespaceMatcher = NON_WHITESPACE_PATTERN.matcher(mSource.getProcessedContent());
		if (nonWhitespaceMatcher.find(errorOffset)) {
			errorOffset = nonWhitespaceMatcher.start();
		}

		mErrorOffset = errorOffset;
	}

	@Override
	public String getMessage() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(mDescription);
		buffer.append(System.lineSeparator());

		String content = mSource.getOriginalContent();

		String[] lines = content.split("\n");
		int errorLine;
		int errorOffsetRemaining = mErrorOffset;
		for (errorLine = 0; errorLine < lines.length; errorLine++) {
			errorOffsetRemaining -= lines[errorLine].length() + 1;
			if (errorOffsetRemaining < 0) {
				break;
			}
		}

		int tailLine = Math.max(0, errorLine - 2);
		int headLine = Math.min(lines.length - 1, errorLine + 2);

		for (int line = tailLine; line <= headLine; line++) {
			buffer.append(mSource.getFileName())
					.append(":")
					.append(line)
					.append(line == errorLine ? "  ----> | " : "        | ")
					.append(lines[line])
					.append(System.lineSeparator());
		}

		return buffer.toString();
	}
}
