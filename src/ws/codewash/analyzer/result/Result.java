package ws.codewash.analyzer.result;


import ws.codewash.analyzer.reports.Report;
import ws.codewash.java.CompilationUnit;
import ws.codewash.java.Location;
import ws.codewash.java.ParsedSourceTree;
import ws.codewash.parser.input.InputElement;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Result {
	public final Map<String, List<Input>> sources;

	public final List<Report> reports;

	public Result(ParsedSourceTree parsedSourceTree, List<ws.codewash.analyzer.reports.Report> extReports) {
		sources = parsedSourceTree.getSources().stream()
				.collect(Collectors.toMap(CompilationUnit::getFileName,
						c -> c.getInputElements().stream()
								.map(Input::new)
								.collect(Collectors.toList())));

		reports = extReports.stream()
				.map(Report::new)
				.collect(Collectors.toList());

	}

	public static class Input {
		public final String v;
		public final int t;

		public Input(InputElement inputElement) {
			this.v = inputElement.getRawValue();
			this.t = 0;
		}

	}

	public static class Report {
		public final String type;
		public final String severity;
		public final Location location;

		public Report(ws.codewash.analyzer.reports.Report report) {
			this.type = report.getCodeSmell();
			this.severity = report.getWarning();
			this.location = report.getLocation();
		}

	}
}
