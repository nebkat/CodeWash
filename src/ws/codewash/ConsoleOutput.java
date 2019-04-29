package ws.codewash;

import ws.codewash.analyzer.reports.Report;
import ws.codewash.java.Location;
import ws.codewash.parser.input.InputElement;
import ws.codewash.parser.input.WhiteSpace;
import ws.codewash.util.Color;

import java.util.List;

import static ws.codewash.util.Color.*;

public class ConsoleOutput {
	public static void print(List<Report> reports) {
		for (Report report : reports) {
			Location location = report.getLocation();

			if (location == null) {
				continue;
			}

			System.out.println();
			System.out.println(WHITE_BOLD + report.getCodeSmell() + ":" + report.getWarning());
			System.out.println(WHITE + "In " + location
					.unit
					.getPath()
					.toString() + ":");

			List<InputElement> inputElements = location.unit.getInputElements();

			int startElement = location.startElement;
			int endElement = location.endElement;

			if (endElement - startElement > 100) {
				endElement = startElement + 100;
			}

			int lineBreaksRemaining = 10;
			int startPrintElement = startElement;
			for (int i = startElement; i > 0 && lineBreaksRemaining > 0; i--) {
				startPrintElement = i;

				if (inputElements.get(i) instanceof WhiteSpace) {
					lineBreaksRemaining--;
				}
			}

			lineBreaksRemaining = 10;
			int endPrintElement = endElement;
			for (int i = endElement; i < inputElements.size() && lineBreaksRemaining > 0; i++) {
				endPrintElement = i;

				if (inputElements.get(i) instanceof WhiteSpace) {
					lineBreaksRemaining--;
				}
			}



			for (int i = startPrintElement; i < endPrintElement; i++) {
				boolean bold = i >= location.startElement && i <= location.endElement;

				if (i == location.startElement) System.out.print(WHITE_BOLD + ">>>" + RESET);
				System.out.print(inputElements.get(i).getPrintingColor(bold) + inputElements.get(i).getRawValue() + RESET);
				if (i == location.endElement) System.out.print(WHITE_BOLD + "<<<" + RESET);
			}

			System.out.println();
		}
	}
}
