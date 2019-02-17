package ws.codewash.parser;

import com.florianingerl.util.regex.Matcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Pattern {
	private static Map<String, String> sDefines = new HashMap<>();

	private static final com.florianingerl.util.regex.Pattern PATTERN_NAME_REGEX = com.florianingerl.util.regex.Pattern.compile("^\\(\\?<(?<name>[a-zA-Z_]+)>.*\\)");
	private static final com.florianingerl.util.regex.Pattern PATTERN_INCLUDE_REGEX = com.florianingerl.util.regex.Pattern.compile("\\(\\?'(?<name>[a-zA-Z_]+)'\\)");

	private com.florianingerl.util.regex.Pattern mPattern;
	private String mName;
	private String mString;
	private List<String> mImports = new ArrayList<>();

	static Pattern compile(String pattern) {
		Pattern p = new Pattern();
		p.mString = pattern;

		Matcher patternNameMatcher = PATTERN_NAME_REGEX.matcher(pattern);
		if (patternNameMatcher.matches()) {
			p.mName = patternNameMatcher.group("name");
		}

		return p;
	}

	Pattern register() {
		if (mName == null) {
			// TODO: Handle
			throw new IllegalStateException("Attempting to register unnamed pattern");
		}
		sDefines.put(mName, "(?(DEFINE)" + mString + ")");

		return this;
	}

	public Matcher matcher(CharSequence input) {
		if (mPattern == null) {
			StringBuilder pattern = new StringBuilder(mString);

			Set<String> included = new HashSet<>();
			included.add(mName);

			boolean added;
			do {
				added = false;

				Matcher patternIncludeMatcher = PATTERN_INCLUDE_REGEX.matcher(pattern.toString());
				while (patternIncludeMatcher.find()) {
					String includeName = patternIncludeMatcher.group("name");
					if (included.contains(includeName)) {
						continue;
					}

					if (sDefines.containsKey(includeName)) {
						included.add(includeName);
						pattern.insert(0, sDefines.get(includeName));
						added = true;
					}
				}
			} while (added);

			try {
				mPattern = com.florianingerl.util.regex.Pattern.compile(pattern.toString());
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println(String.join("\n", mImports) + "\n" + mString);
				// TODO:
			}
		}
		return mPattern.matcher(input);
	}

	@Override
	public String toString() {
		return mString;
	}
}
