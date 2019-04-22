package ws.codewash.analyzer.reports;

import ws.codewash.analyzer.smells.Smell;
import ws.codewash.java.CWClassOrInterface;
import ws.codewash.java.CWMember;

import java.util.List;

public class MemberReport implements Report {

	private Smell mCodeSmell;
	private CWClassOrInterface mReportClass;
	private List<CWMember> mProblemMembers;
	private Warning mSmellWarning;

	public MemberReport(Smell codeSmell, CWClassOrInterface reportClass, List<CWMember> problemMembers, Warning warning) {
		mCodeSmell = codeSmell;
		mReportClass = reportClass;
		mProblemMembers = problemMembers;
		mSmellWarning = warning;
	}

}
