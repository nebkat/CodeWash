package ws.codewash.analyzer.reports;

import ws.codewash.java.CWClassOrInterface;
import ws.codewash.java.CWMember;

import java.util.List;

/**
 * Used to store the data relating to Code Smells relating to Members of Classes.
 */
public class MemberReport implements Report {

	/**
	 * The name of the Code Smell being reported on.
	 */
	private String mCodeSmell;

	/**
	 * The class containing the problem members.
	 */
	private CWClassOrInterface mReportClass;

	/**
	 * List of the members in which there is a Code Smell.
	 */
	private List<CWMember> mProblemMembers;

	/**
	 * The warning associated with the Code Smell.
	 */
	private Warning mSmellWarning;

	/**
	 * Constructs a report with the values passed through to it.
	 *
	 * @param codeSmell      The name of the Code Smell associated with the report
	 * @param problemMembers The list of problem members.
	 * @param warning        The warning associated with the smell.
	 */
	public MemberReport(String codeSmell, CWClassOrInterface reportClass, List<CWMember> problemMembers, Warning warning) {
		mCodeSmell = codeSmell;
		mReportClass = reportClass;
		mProblemMembers = problemMembers;
		mSmellWarning = warning;
	}

}
