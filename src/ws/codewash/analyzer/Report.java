package ws.codewash.analyzer;

public class Report {

	private String mReportName;
	private String mReportBody;
	private Warning mWarningLevel;

	public Report(String name, Warning warning) {
		mReportName = name;
		mWarningLevel = warning;
	}

	public String toString() {
		String report = "\n---" + mReportName + "---\n" +
				"Warning Level : " + mWarningLevel + "\n";
		return report;
	}

	public enum Warning {
		ISSUE, WARNING
	}
}
