package ws.codewash.analyzer;

import ws.codewash.analyzer.reports.Report;
import ws.codewash.analyzer.smells.CodeSmell;
import ws.codewash.parser.ParsedSourceTree;
import ws.codewash.util.Config;
import ws.codewash.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Analyzer {
	private final static String TAG = "ANALYZER";
	private List<CodeSmell> mCodeSmells = new ArrayList<>();

	public Analyzer(ParsedSourceTree parsedSourceTree) {
		try {
			for (String s : Config.get().getSmells().keySet()) {
				mCodeSmells.add(Config.get().getSmells().get(s).apply(parsedSourceTree));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			Log.i(TAG, "Checking for the Following Smell:");
			for (CodeSmell codeSmell : mCodeSmells) {
				Log.i(TAG, "- " + codeSmell.getName());
			}
		}
	}

	public List<Report> analyse() {
		List<Report> reports = new ArrayList<>();
		mCodeSmells.parallelStream().forEach(codeSmell -> reports.addAll(codeSmell.run()));
		return reports;
	}
}
