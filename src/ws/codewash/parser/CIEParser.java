package ws.codewash.parser;

import ws.codewash.java.*;
import ws.codewash.reader.Source;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CIEParser extends Parser {
	private final Pattern mClassPattern = Pattern.compile("\\s*class\\s+(?<name>[a-zA-Z_][a-zA-Z0-9_]*)\\s*" +
			"(\\s+extends\\s+(?<super>[a-zA-Z_][a-zA-Z0-9_]*))?\\s*" +
			"(\\s+implements\\s+(?<interface>[a-zA-Z_][a-zA-Z0-9_]*\\s*(?:,\\s*[a-zA-Z_][a-zA-Z0-9_]*)*))?\\s*\\{");

	private final Pattern mInterfacePattern = Pattern.compile("\\s*interface\\s+(?<name>[a-zA-Z_][a-zA-Z0-9_]*)\\s+" +
			"(extends\\s+(?<interface>[a-zA-Z_][a-zA-Z0-9_]*\\s*(?:,\\s*[a-zA-Z_][a-zA-Z0-9_]*)*))?\\s*");


	Map<String, CWAbstractClass> parseAbstractClass(CWSourceTree sourceTree, Map<Source, String> sources) {
		Map<String, CWAbstractClass> classes = new HashMap<>();

		for (Source s : sources.keySet()) {
			CWPackage cwPackage = null;
			CWAccessModifier accessModifier = null;
			boolean _static = false;
			boolean _final = false;
			boolean _abstract = false;
			Scanner scanner = new Scanner(sources.get(s));

			String source = sources.get(s);
			Matcher packageMatcher = mPackagePattern.matcher(source);
			Matcher classMatcher = mClassPattern.matcher(source);
			Matcher modifierMatcher = mModifierPattern.matcher(source);
			Matcher openMatcher = mOpenBrace.matcher(source);
			Matcher closeMatcher = mCloseBrace.matcher(source);

			System.out.println(s.getName());

			while (source.length() > 0) {
				if (packageMatcher.find() || classMatcher.find() || modifierMatcher.find()) {
					packageMatcher.reset();
					classMatcher.reset();
					modifierMatcher.reset();
					if (packageMatcher.find() && packageMatcher.start() == 0) {
						if (cwPackage == null) {
							cwPackage = sourceTree.getPackages().get(packageMatcher.group(Keywords.PACKAGE));
							source = source.substring(packageMatcher.end());
							System.out.println(cwPackage);
						} else {
							System.err.println("Error parsing " + s.getName() + " | Package statement already declared..");
							break;
						}
					} else if (classMatcher.find() && classMatcher.start() == 0) {
						//System.out.println(classMatcher.group());
						String name = classMatcher.group("name");
						String fullName = cwPackage == null ? name : cwPackage.getName() + "." + name;

						System.out.println("\n\n" + " ");
						classes.put(fullName, new CWClass(cwPackage,accessModifier,_final,_abstract,_static,name));
						System.out.println(classes.get(fullName));
						if (classMatcher.group("super") != null) {
							System.out.println("Extends: " + classMatcher.group("super"));
						}
						if (classMatcher.group("interface") != null) {
							System.out.println("Implements: " + classMatcher.group("interface"));
						}

						accessModifier = null;
						_static = _final = _abstract = false;
						source = source.substring(classMatcher.end());
					} else if (modifierMatcher.find()) {
						String modifier = modifierMatcher.group().replaceAll("\\s","");
						switch (modifier) {
							case Keywords.ABSTRACT:
								_abstract = true;
								_final = false;
								break;
							case Keywords.FINAL:
								_final = true;
								_abstract = false;
								break;
							case Keywords.PRIVATE:
								accessModifier = CWAccessModifier.PRIVATE;
								break;
							case Keywords.PROTECTED:
								accessModifier = CWAccessModifier.PROTECTED;
								break;
							case Keywords.PUBLIC:
								accessModifier = CWAccessModifier.PUBLIC;
								break;
							case Keywords.STATIC:
								_static = true;
								break;
						}
						source = source.substring(modifierMatcher.end());
					} else if (openMatcher.find()) {
						accessModifier = null;
						_static = _final = _abstract = false;

						source = source.substring(modifierMatcher.end());
					}
				} else {
					source = "";
				}

				packageMatcher.reset(source);
				classMatcher.reset(source);
				openMatcher.reset(source);
				closeMatcher.reset(source);
				modifierMatcher.reset(source);
			}
		}

		return classes;
	}
}