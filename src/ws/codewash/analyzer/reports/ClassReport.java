package ws.codewash.analyzer.reports;

import ws.codewash.analyzer.smells.Smell;
import ws.codewash.java.CWClassOrInterface;

public class ClassReport implements Report {

	private Smell mCodeSmell;
	private CWClassOrInterface mProblemClasses;
	private Warning mSmellWarning;

	public ClassReport(Smell codeSmell, CWClassOrInterface problemClass, Warning warning) {
		mCodeSmell = codeSmell;
		mProblemClasses = problemClass;
		mSmellWarning = warning;
	}

}
