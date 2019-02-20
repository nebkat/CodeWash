package ws.codewash.analyzer;

import ws.codewash.analyzer.smells.CodeSmell;
import ws.codewash.analyzer.smells.LongMethods;
import ws.codewash.analyzer.smells.LongParameterList;
import ws.codewash.parser.ParsedSourceTree;
import ws.codewash.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Analyzer {

	private final static String TAG = "ANALYZER";

	private static final Map<String, Function<ParsedSourceTree, CodeSmell>> CODE_SMELLS = new HashMap<>() {{
		put(LongMethods.NAME, LongMethods::new);
		//put(RefusedBequest.NAME, RefusedBequest::new);
		//put(PrimitiveObsession.NAME, PrimitiveObsession::new);
		put(LongParameterList.NAME, LongParameterList::new);
	}};

	private List<CodeSmell> mCodeSmells = new ArrayList<>();

	public Analyzer(ParsedSourceTree parsedSourceTree) {
		try {
			for (String s : CODE_SMELLS.keySet()) {
				mCodeSmells.add(CODE_SMELLS.get(s).apply(parsedSourceTree));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			Log.i(TAG, "Checking for the Following Smells:");
			for (CodeSmell codeSmell : mCodeSmells) {
				System.out.println("- " + codeSmell.getName());
			}
		}
	}

	public List<Report> analyse() {
		return mCodeSmells.parallelStream().map(CodeSmell::run).collect(Collectors.toList());
	}
}
