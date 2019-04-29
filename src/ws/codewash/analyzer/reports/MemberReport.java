package ws.codewash.analyzer.reports;

import ws.codewash.java.CWClassOrInterface;
import ws.codewash.java.CWMember;


/**
 * Used to store the data relating to Code Smells relating to Members of Classes.
 */
public class MemberReport extends Report {


	/**
	 * Member in which there is a Code Smell.
	 */
	private CWMember mProblemMember;

	/**
	 * The warning associated with the Code Smell.
	 */
	private Warning mSmellWarning;

	/**
	 * Constructs a report with the values passed through to it.
	 *
	 * @param codeSmell      The name of the Code Smell associated with the report
	 * @param problemMember The list of problem members.
	 * @param warning        The warning associated with the smell.
	 */
	public MemberReport(String codeSmell, CWClassOrInterface reportClass, CWMember problemMember, Warning warning) {
		super(codeSmell, warning.toString(), problemMember.getLocation());
		mProblemMember = problemMember;
		mSmellWarning = warning;
	}

	public CWMember getProblemMembers() {
		return mProblemMember;
	}

	public String getWarning() {
		return mSmellWarning.toString();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getCodeSmell());
		sb.append("\n");
//		for (CWMember member : mProblemMembers) {
//			sb.append("\tClass = ");
//			sb.append(mReportClass.getSimpleName());
//
//			if (member instanceof CWMethod) {
//				sb.append(" - Method = ");
//			} else {
//				sb.append(" - Field = ");
//			}
//
//			sb.append(member.getName());
//			sb.append("\n");
//		}

		sb.append(getProblemMembers().getName());
		sb.append("\n");
		return sb.toString();
	}

}
