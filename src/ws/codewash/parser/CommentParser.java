package ws.codewash.parser;

import ws.codewash.reader.Source;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentParser extends Parser {
	private static final Pattern OPEN_COMMENT = Pattern.compile("/\\*");
	private static final Pattern CLOSE_COMMENT = Pattern.compile("\\*/");
	private static final Pattern LINE_COMMENT = Pattern.compile("\\s*//");
	private boolean mComment;

	public Map<Source,String> parseComments(List<Source> sources) {
		Map<Source, String> newSources = new HashMap<>();

		for (Source s : sources) {
			StringBuilder content = new StringBuilder();
			mComment = false;

			for (String line : s) {
				content.append(parseLine(line)).append("\n");
			}
			newSources.put(s, content.toString());
		}
		return newSources;
	}

	private String parseLine(String line) {
		Matcher lineComment = LINE_COMMENT.matcher(line);
		Matcher openComment = OPEN_COMMENT.matcher(line);
		Matcher closeComment = CLOSE_COMMENT.matcher(line);
		if (!mComment) {
			if (lineComment.find()) {
				return parseLine(line.substring(0,lineComment.start()));
			} else if (openComment.find()) {
				mComment = true;
				return line.substring(0,openComment.start()) + parseLine(line.substring(openComment.start()));
			}
		} else {
			if (closeComment.find()) {
				mComment = false;
				int index = closeComment.start()+2;

				return emptyString(index) + parseLine(line.substring(index));
			} else {
				return "";
			}
		}
		return line;
	}

	private String emptyString(int length) {
		StringBuilder empty = new StringBuilder();
		for (int i = 0 ; i < length ; i++) {
			empty.append(" ");
		}
		return empty.toString();
	}
}
