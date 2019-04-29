package ws.codewash.analyzer.reports;

import ws.codewash.java.CWClassOrInterface;


/**
 * Used to store the data relating to Code Smells relating to Classes.
 */
public class ClassReport extends Report {

	/**
	 * Constructs a report with the values passed through to it.
	 *
	 * @param codeSmell    The name of the Code Smell associated with the report
	 * @param problemClass The class in which there is the Code Smell
	 * @param warning      The warning associated with the smell.
	 */
	public ClassReport(String codeSmell, Warning warning,CWClassOrInterface problemClass) {
		super(codeSmell, warning.toString(), problemClass.getLocation());
	}

	@Override
	public String toString() {
		return getCodeSmell() + " " + getLocation().unit.getFileName() ;
	}

}
