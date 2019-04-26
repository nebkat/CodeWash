package ws.codewash.analyzer.reports;

import ws.codewash.java.CWClassOrInterface;

/**
 * Used to store the data relating to Code Smells relating to Classes.
 */
public class ClassReport implements Report {

	/**
	 * The name of the Code Smell being reported on.
	 */
	private String mCodeSmell;

	/**
	 * The class in which there is an issue.
	 */
	private CWClassOrInterface mProblemClasses;

	/**
	 * The warning associated with the Code Smell
	 */
	private Warning mSmellWarning;

	/**
	 * Constructs a report with the values passed through to it.
	 *
	 * @param codeSmell    The name of the Code Smell associated with the report
	 * @param problemClass The class in which there is the Code Smell
	 * @param warning      The warning associated with the smell.
	 */
	public ClassReport(String codeSmell, CWClassOrInterface problemClass, Warning warning) {
		mCodeSmell = codeSmell;
		mProblemClasses = problemClass;
		mSmellWarning = warning;
	}

}
