package ws.codewash.analyzer.reports;

/**
 * Interface used to allow the storage of the different types of report in a single data structure.
 */
public interface Report {
	String getCodeSmell();
	String getWarning();
}
