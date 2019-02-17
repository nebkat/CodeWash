package ws.codewash.parser;

public class CommentPreProcessor implements PreProcessor {
	private static final String BLOCK_COMMENT_OPEN = "/*";
	private static final String BLOCK_COMMENT_CLOSE = "*/";
	private static final String LINE_COMMENT_OPEN = "//";
	private static final String LINE_COMMENT_CLOSE = "\n";

	@Override
	public String process(String input) {
		StringBuilder buffer = new StringBuilder(input);

		int index = 0;
		int eofIndex = buffer.length() - 1;

		while (index < eofIndex) {
			// Search for block or line comment
			int blockIndex = buffer.indexOf(BLOCK_COMMENT_OPEN, index);
			int lineIndex = buffer.indexOf(LINE_COMMENT_OPEN, index);

			if (blockIndex < 0 && lineIndex < 0) break;
			if (blockIndex < 0) blockIndex = Integer.MAX_VALUE;
			if (lineIndex < 0) lineIndex = Integer.MAX_VALUE;

			// Find closing sequence
			String closePattern = blockIndex < lineIndex ? BLOCK_COMMENT_CLOSE : LINE_COMMENT_CLOSE;
			int openIndex = Math.min(blockIndex, lineIndex);
			int closeIndex = buffer.indexOf(closePattern, openIndex);
			if (closeIndex < 0) {
				closeIndex = eofIndex;
			} else {
				closeIndex += closePattern.length();
			}

			// Replace non whitespace characters with space
			for (int i = openIndex; i < closeIndex; i++) {
				if (!Character.isWhitespace(buffer.charAt(i))) {
					buffer.setCharAt(i, ' ');
				}
			}

			index = closeIndex;
		}

		return buffer.toString();
	}
}
