package ws.codewash.analyzer.reports;

import ws.codewash.java.CWClassOrInterface;
import ws.codewash.java.Location;

/**
 * Interface used to allow the storage of the different types of report in a single data structure.
 */
public abstract class Report {

	private String mCodeSmell;
	private String mWarning;

	private Location mLocation;

	public Report(String codeSmell, String warning, Location location) {
		mCodeSmell = codeSmell;
		mWarning = warning;
		mLocation = location;
	}

	public String getCodeSmell() {
		return mCodeSmell;
	}

	public String getWarning() {
		return mWarning;
	}

	public Location getLocation() {
		return mLocation;
	}

}
