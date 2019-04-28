package ws.codewash.analyzer.reports;

import ws.codewash.java.CWClassOrInterface;
import ws.codewash.java.CWMember;
import ws.codewash.java.CWMethod;

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

	public String getCodeSmell() {
		return mCodeSmell;
	}

	public CWClassOrInterface getReportClass() {
		return mReportClass;
	}

	public List<CWMember> getProblemMembers() {
		return mProblemMembers;
	}

	public String getWarning() {
		return mSmellWarning.toString();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(mCodeSmell);
		sb.append("\n");
		for (CWMember member : mProblemMembers) {
			sb.append("\tClass = ");
			sb.append(mReportClass.getSimpleName());

			if (member instanceof CWMethod) {
				sb.append(" - Method = ");
			} else {
				sb.append(" - Field = ");
			}

			sb.append(member.getName());
			sb.append("\n");
		}

		return sb.toString();
	}

}
