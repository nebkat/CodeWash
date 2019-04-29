package ws.codewash.analyzer.smells.disposables;

import ws.codewash.analyzer.reports.ClassReport;
import ws.codewash.analyzer.reports.Report;
import ws.codewash.analyzer.reports.Warning;
import ws.codewash.analyzer.smells.CodeSmell;
import ws.codewash.java.CWMethod;
import ws.codewash.java.ParsedSourceTree;
import ws.codewash.util.config.Config;

import java.util.ArrayList;
import java.util.List;

public class LazyClass extends CodeSmell {

	public static final String NAME = "LazyClass";

	private final String CONFIG_LAZY_LENTH = "MinLength";
	private final int MIN_LENGTH;

	public LazyClass(ParsedSourceTree parsedSourceTree) {
		super(parsedSourceTree);
		MIN_LENGTH = Config.get().LazyClassConfig(CONFIG_LAZY_LENTH).intValue();
	}

	/**
	 * Defines the procedure to detect a Lazy Class
	 *
	 * @return A list of {@link Report} which details problem Classes / Members
	 */
	@Override
	public List<Report> run() {
		List<Report> reports = new ArrayList<>();
		super.getParsedSourceTree().getClasses().forEach((key, value) -> {

			if (!value.isExternal() && value.getOuterClass() == null) {

				int totalFields = value.getFields().size();
				int totalMethods = value.getMethods().size();

				int totalMethodLength = 0;

				for (CWMethod cwMethod : value.getMethods()) {
					totalMethodLength += cwMethod.getMethodLength();
				}

				if (totalFields + totalMethods + totalMethodLength < MIN_LENGTH) {
					reports.add(new ClassReport(NAME, value, Warning.ISSUE));
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


}
