package ws.codewash.analyzer.result;

import ws.codewash.java.Location;

import java.util.List;
import java.util.Map;

public class Result {
	public final Map<String, List<Input>> sources;

	public final List<Report> reports;

	public Result(Map<String, List<Input>> sources, List<Report> reports) {
		this.sources = sources;
		this.reports = reports;
	}

	public static class Input {
		public final String v;
		public final int t;

		public Input(String v, int t) {
			this.v = v;
			this.t = t;
		}
	}

	public static class Report {
		public final String type;
		public final String severity;
		public final Location location;

		public Report(String type, String severity, Location location) {
			this.type = type;
			this.severity = severity;
			this.location = location;
		}
	}
}
