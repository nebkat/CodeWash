package ws.codewash.analyzer.reports;

import ws.codewash.parser.input.Literal;

public class LiteralReport extends Report {

	private Literal mProblemLiteral;

	public LiteralReport(String codeSmell, Warning warning, Literal literal) {
		super(codeSmell, warning.toString(), literal.getLocation());
		mProblemLiteral = literal;
	}

	@Override
	public String toString() {
		return getCodeSmell() + " " + mProblemLiteral.getRawValue();
	}

}
