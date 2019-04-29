package ws.codewash.analyzer.reports;

import ws.codewash.parser.input.Literal;

public class LiteralReport extends Report {

	private Literal problemLiteral;

	public LiteralReport(String codeSmell, Warning warning, Literal literal) {
		super(codeSmell, warning.toString(), literal.getLocation());
	}

}
