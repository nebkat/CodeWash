package ws.codewash.analyzer.smells;

import ws.codewash.analyzer.reports.Report;
import ws.codewash.parser.ParsedSourceTree;

import java.util.List;

/**Abstract Code Smell class used by all Code Smells*/
public abstract class CodeSmell {

	/**Parsed Source Tree structure used to retrieve all data relating to classes, etc.*/
	private ParsedSourceTree mParsedSourceTree;

	/**
	 * Constructs a CodeSmell Object with a {@link ws.codewash.parser.ParsedSourceTree} object
	 * @param parsedSourceTree The {@link ws.codewash.parser.ParsedSourceTree} to check for code smells
	 */
	protected CodeSmell(ParsedSourceTree parsedSourceTree) {
		mParsedSourceTree = parsedSourceTree;
	}

	/**
	 * Defines the procedure to detect each Code Smell.
	 * Each Code Smell will implement its own procedure
	 * @return A list of {@link ws.codewash.analyzer.reports.Report} which details problem Classes / Members
	 */
	public abstract List<Report> run();

	/**
	 * Retrieves the name associated with each Code Smell.
	 * @return The name of the Code Smell.
	 */
	public abstract String getName();

	/**
	 * Retrieves the {@link ws.codewash.parser.ParsedSourceTree} object.
	 * @return The {@link ws.codewash.parser.ParsedSourceTree} of the Code Smell.
	 */
	protected ParsedSourceTree getParsedSourceTree() {
		return mParsedSourceTree;
	}
}

