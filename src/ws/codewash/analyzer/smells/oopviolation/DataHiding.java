package ws.codewash.analyzer.smells.oopviolation;

import ws.codewash.analyzer.reports.MemberReport;
import ws.codewash.analyzer.reports.Report;
import ws.codewash.analyzer.reports.Warning;
import ws.codewash.analyzer.smells.CodeSmell;
import ws.codewash.java.CWMember;
import ws.codewash.java.ParsedSourceTree;
import ws.codewash.util.Log;
import ws.codewash.util.config.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class used to manage the Violations of Data Hiding Code Smell
 */
public class DataHiding extends CodeSmell {

	/**
	 * Name of the class, used for reports.
	 */
	public static final String NAME = "DataHiding";

	private Config mConfig = ws.codewash.util.config.Config.get().configs.dataHiding;

	/**
	 * Constructs a DataHiding object with a {@link ws.codewash.java.ParsedSourceTree} object.
	 * @param parsedSourceTree The {@link ws.codewash.java.ParsedSourceTree} object to detect Violations of DataHiding in.
	 */
	public DataHiding(ParsedSourceTree parsedSourceTree) {
		super(parsedSourceTree);
	}

	/**
	 * Defines the procedure to detect ViolationsOfDataHiding.
	 *
	 * @return A list of {@link Report} which details problem members
	 */
	@Override
	public List<Report> run() {
		Log.i(NAME.toUpperCase(), "Running Data Hiding check. | Params: None.");

		List<Report> reports = new ArrayList<>();

		/*
			For each class, go through each field and see if it just has public access
			If so add it to the list
		*/
		super.getParsedSourceTree().getClasses().forEach((key, cwClass) -> {
			List<CWMember> problemFields = new ArrayList<>();
			problemFields.addAll(cwClass.getFields()
					.parallelStream()
					.filter(cwField -> cwField.isPublic() && !cwField.isStatic() && !cwField.isFinal())
					.collect(Collectors.toList()));

			if (!problemFields.isEmpty()) {
				problemFields.forEach(cwMember -> reports.add(new MemberReport(NAME, cwClass, cwMember, Warning.ISSUE)));

			}
		});

		return reports;
	}

	/**
	 * Retrieves the name associated with DataHiding.
	 *
	 * @return The name of the Code Smell.
	 */
	@Override
	public String getName() {
		return NAME;
	}

	public static class Config {}
}
