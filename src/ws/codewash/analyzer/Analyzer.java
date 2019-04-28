package ws.codewash.analyzer;

import ws.codewash.analyzer.reports.Report;
import ws.codewash.analyzer.smells.CodeSmell;
import ws.codewash.java.ParsedSourceTree;
import ws.codewash.util.Config;
import ws.codewash.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Manages all of the Code Smells and how they are run, including the config options for each smell.
 */
public class Analyzer {

	/**
	 * String representing the class
	 */
	private final static String TAG = "Analyzer";

	/**
	 * List of all the Code Smells that have been selected in the config to be run.
	 */
	private List<CodeSmell> mCodeSmells = new ArrayList<>();

	/**
	 * Constructs an Analyzer object with the {@link ws.codewash.parser.ParsedSourceTree} object.
	 * @param parsedSourceTree The {@link ws.codewash.parser.ParsedSourceTree} object to be analyzed.
	 */
	public Analyzer(ParsedSourceTree parsedSourceTree) {
		// Get all of the code smells from the config
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

	/**
	 * Method used to run all of the selected Code Smells and get the reports generated
	 * @return A list of type {@link ws.codewash.analyzer.reports.Report} containing all of the Code Smell reports generated.
	 */
	public List<Report> analyse() {
		List<Report> reports = new ArrayList<>();
		mCodeSmells.parallelStream().forEach(codeSmell -> reports.addAll(codeSmell.run()));
		reports.sort(Comparator.comparing(Report::getCodeSmell));
		return reports;
	}
}
