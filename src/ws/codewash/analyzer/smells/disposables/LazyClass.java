package ws.codewash.analyzer.smells.disposables;

import ws.codewash.analyzer.reports.ClassReport;
import ws.codewash.analyzer.reports.Report;
import ws.codewash.analyzer.reports.Warning;
import ws.codewash.analyzer.smells.CodeSmell;
import ws.codewash.java.CWMethod;
import ws.codewash.java.ParsedSourceTree;
import ws.codewash.util.Log;
import ws.codewash.util.config.Config;

import java.util.ArrayList;
import java.util.List;

public class LazyClass extends CodeSmell {

	public static final String NAME = "LazyClass";

	private Config mConfig = ws.codewash.util.config.Config.get().configs.lazyClass;

	public LazyClass(ParsedSourceTree parsedSourceTree) {
		super(parsedSourceTree);
	}

	/**
	 * Defines the procedure to detect a Lazy Class
	 *
	 * @return A list of {@link Report} which details problem Classes / Members
	 */
	@Override
	public List<Report> run() {
		Log.i(NAME.toUpperCase(), "Running Lazy Classes check.\t| Params: Min Length = " + mConfig.minLength);


		List<Report> reports = new ArrayList<>();
		super.getParsedSourceTree().getClasses().forEach((key, value) -> {

			if (!value.isExternal() && value.getOuterClass() == null) {

				int totalFields = value.getFields().size();
				int totalMethods = value.getMethods().size();

				int totalMethodLength = 0;

				for (CWMethod cwMethod : value.getMethods()) {
					totalMethodLength += cwMethod.getMethodLength();
				}

				if (totalFields + totalMethods + totalMethodLength < mConfig.minLength) {
					reports.add(new ClassReport(NAME, Warning.ISSUE, value));
				}
			}
		});

		return reports;
	}

	/**
	 * Retrieves the name associated with the Code Smell
	 *
	 * @return The name of the Code Smell.
	 */
	@Override
	public String getName() {
		return NAME;
	}


	public static class Config {
		private final int minLength = 15;
	}
}
