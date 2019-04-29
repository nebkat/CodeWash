package ws.codewash.analyzer.smells.oopviolation;

import ws.codewash.analyzer.reports.ClassReport;
import ws.codewash.analyzer.reports.Report;
import ws.codewash.analyzer.reports.Warning;
import ws.codewash.analyzer.smells.CodeSmell;
import ws.codewash.java.CWClass;
import ws.codewash.java.CWClassOrInterface;
import ws.codewash.java.CWVoid;
import ws.codewash.java.ParsedSourceTree;
import ws.codewash.java.statement.CWReturnStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class DataClass extends CodeSmell {

	/**
	 * The name of the Code Smell. Used in reports.
	 */
	public static final String NAME = "DataClass";

	private Config mConfig = ws.codewash.util.config.Config.get().configs.dataClass;

	/**
	 * Constructs a CodeSmell Object with a {@link ParsedSourceTree} object
	 *
	 * @param parsedSourceTree The {@link ParsedSourceTree} to check for code smells
	 */
	public DataClass(ParsedSourceTree parsedSourceTree) {
		super(parsedSourceTree);
	}

	@Override
	public List<Report> run() {
		List<Report> reports = new ArrayList<>();

		getParsedSourceTree().getClasses().values().stream()
				.filter(CWClassOrInterface::isInternal)
				.filter(CWClass.class::isInstance)
				.filter(Predicate.not(CWClassOrInterface::hasInterfaces))
				.map(CWClass.class::cast)
				.filter(Predicate.not(CWClass::hasSuperClass))
				.forEach(cwClass -> {
					if (!cwClass.getFields().isEmpty()) {
						// If all methods match getters or setters
						boolean allMatch = cwClass.getMethods().stream().allMatch(m -> {
							// not including tostring
							if (m.getName().equalsIgnoreCase("toString"))
								return true;
							boolean fieldMethod = false;

							//	Checking if method returns a field
							if (m.getReturnType() != CWVoid.VOID) {
								if (m.getBlock() != null)
									fieldMethod = m.getBlock().getStatements()
											.stream()
											.filter(CWReturnStatement.class::isInstance)
											.anyMatch(s -> cwClass.getFields()
													.stream()
													.anyMatch(f -> s.getNode().getContent().toLowerCase().contains(f.getName().toLowerCase())
															&& f.getType().equals(m.getReturnType())));
							}

							return fieldMethod || m.getName().toLowerCase().startsWith("get") || m.getName().toLowerCase().startsWith("set");
						});

						if (allMatch)
							reports.add(new ClassReport(NAME, cwClass, Warning.ISSUE));
					}
				});
		return reports;
	}

	@Override
	public String getName() {
		return NAME;
	}

	public static class Config {
	}
}
