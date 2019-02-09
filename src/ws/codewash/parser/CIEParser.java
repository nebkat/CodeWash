package ws.codewash.parser;

import ws.codewash.java.CWAbstractClass;
import ws.codewash.java.CWAccessModifier;
import ws.codewash.java.CWPackage;
import ws.codewash.java.CWSourceTree;
import ws.codewash.reader.Source;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CIEParser extends Parser {
	private final Pattern mClassPattern = Pattern.compile("\\s*class\\s+(?<name>[a-zA-Z_][a-zA-Z0-9_]*)\\s" +
			"+(extends\\s+(?<super>[a-zA-Z_][a-zA-Z0-9_]*))?\\s" +
			"*(implements\\s*(?<interface>[a-zA-Z_][a-zA-Z0-9_]*(?:,[a-zA-Z_][a-zA-Z0-9_]*)*))?\\s*\\{");

	private final Pattern mInterfacePattern = Pattern.compile("\\s*interface\\s+(?<name>[a-zA-Z_][a-zA-Z0-9_]*)\\s" +
			"(extends\\s+(?<interface>[a-zA-Z_][a-zA-Z0-9_]*(?:,[a-zA-Z_][a-zA-Z0-9_]*)*))?\\s*\\{");


	Map<String, CWAbstractClass> parseAbstractClass(CWSourceTree sourceTree, Map<Source, String> sources) {
		Map<String, CWAbstractClass> classes = new HashMap<>();

		for (Source s : sources.keySet()) {
			CWPackage cwPackage;
			CWAccessModifier accessModifier;
			boolean _static;
			boolean _final;
			boolean _abstract;
			Scanner scanner = new Scanner(sources.get(s));

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				Matcher packageLine = mPackagePattern.matcher(line);
				Matcher interfaceLine = mInterfacePattern.matcher(line);
				Matcher classLine = mClassPattern.matcher(line);

				if (packageLine.find()) {
					if (sourceTree.getPackages().containsKey(packageLine.group(Keywords.PACKAGE))) {
						cwPackage = sourceTree.getPackages().get(packageLine.group(Keywords.PACKAGE));
					} else {
						System.err.println("Error parsing: " + s.getName() + " | Could not find package.");
					}
				}

				if (classLine.find()) {
					System.out.println(line);
					System.out.println("Name: " + classLine.group("name"));
					if (classLine.group("super") != null) {
						System.out.println("Extends: " + classLine.group("super"));
					}
					if (classLine.group("interface") != null) {
						System.out.println("Implements: " + classLine.group("interface"));
					}
					System.out.println("\n");
				}
				if (interfaceLine.find()) {
					System.out.println(line);
					System.out.println("Name: " + interfaceLine.group("name"));
					if (interfaceLine.group("interface") != null) {
						System.out.println("Extends: " + interfaceLine.group("super"));
					}
					System.out.println("\n");
				}

			}
		}

		return classes;
	}
}
