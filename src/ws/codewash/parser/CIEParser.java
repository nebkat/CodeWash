package ws.codewash.parser;

import ws.codewash.java.*;
import ws.codewash.reader.Source;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CIEParser extends Parser {
	private final Pattern mClassPattern = Pattern.compile("\\s*class\\s+(?<name>[a-zA-Z_][a-zA-Z0-9_]*)\\s*" +
			"(\\s+extends\\s+(?<super>[a-zA-Z_][a-zA-Z0-9_]*))?\\s*" +
			"(\\s+implements\\s+(?<interface>[a-zA-Z_][a-zA-Z0-9_]*\\s*(?:,\\s*[a-zA-Z_][a-zA-Z0-9_]*)*))?\\s*\\{");

	private final Pattern mInterfacePattern = Pattern.compile("\\s*interface\\s+(?<name>[a-zA-Z_][a-zA-Z0-9_]*)\\s*+" +
			"(\\+extends\\s+(?<interface>[a-zA-Z_][a-zA-Z0-9_]*\\s*(?:,\\s*[a-zA-Z_][a-zA-Z0-9_]*)*))?\\s*");


	Map<String, CWAbstractClass> parseAbstractClass(CWSourceTree sourceTree, Map<Source, String> sources) {
		Map<String, CWAbstractClass> classes = new HashMap<>();
		Map<CWAbstractClass, Map<String,String>> classImports = new HashMap<>();
		Map<CWAbstractClass, List<String>> wildcardImports = new HashMap<>();
		Map<CWAbstractClass, String> extendedClasses = new HashMap<>();
		Map<CWAbstractClass, List<String>> implementedClasses = new HashMap<>();

		for (Source s : sources.keySet()) {
			CWPackage cwPackage = null;
			CWAccessModifier accessModifier = null;
			boolean _static = false;
			boolean _final = false;
			boolean _abstract = false;

			String source = sources.get(s);
			Matcher packageMatcher = mPackagePattern.matcher(source);
			Matcher classMatcher = mClassPattern.matcher(source);
			Matcher modifierMatcher = mModifierPattern.matcher(source);
			Matcher openMatcher = mOpenBrace.matcher(source);
			Matcher closeMatcher = mCloseBrace.matcher(source);

			Map<String, String> imports = new HashMap<>();
			List<String> wildcards = new ArrayList<>();

			Matcher importMatcher = mImportPattern.matcher(source);
			while (importMatcher.find()) {
				String importPackage = importMatcher.group(Keywords.PACKAGE);
				String importName = importPackage.split("\\.")[importPackage.split("\\.").length-1];

				if (importName.equals("*")) {
					wildcards.add(importPackage.substring(0,importPackage.length()-1));
				} else {
					imports.put(importName, importPackage);
				}
			}

			while (source.length() > 0) {
				if (packageMatcher.find() || classMatcher.find() || modifierMatcher.find()) {
					packageMatcher.reset();
					classMatcher.reset();
					modifierMatcher.reset();
					if (packageMatcher.find() && packageMatcher.start() == 0) {
						if (cwPackage == null) {
							cwPackage = sourceTree.getPackages().get(packageMatcher.group(Keywords.PACKAGE));
							source = source.substring(packageMatcher.end());
						} else {
							System.err.println("Error parsing " + s.getName() + " | Package statement already declared..");
							break;
						}
					} else if (classMatcher.find() && classMatcher.start() == 0) {
						String name = classMatcher.group("name");
						String fullName = cwPackage == null ? name : cwPackage.getName() + "." + name;

						classes.put(fullName, new CWClass(cwPackage,accessModifier,_final,_abstract,_static,name));
						classImports.put(classes.get(fullName),imports);
						wildcardImports.put(classes.get(fullName), wildcards);

						if (classMatcher.group(Keywords.SUPER) != null) {
							extendedClasses.put(classes.get(fullName), classMatcher.group(Keywords.SUPER));
						}
						if (classMatcher.group(Keywords.INTERFACE) != null) {
							String interfaceStrings = classMatcher.group(Keywords.INTERFACE);
							List<String> interfaces = new ArrayList<>();
							if (interfaceStrings.split(",\\s*").length > 0) {
								interfaces.addAll(Arrays.asList(interfaceStrings.split(",\\s*")));
							} else {
								interfaces.add(interfaceStrings.replaceAll("\\s",""));
							}
							implementedClasses.put(classes.get(fullName),interfaces);
						}

						accessModifier = null;
						_static = _final = _abstract = false;
						source = source.substring(classMatcher.end());
					} else if (openMatcher.find() && openMatcher.start() == 0) {
						accessModifier = null;
						_static = _final = _abstract = false;

						source = source.substring(openMatcher.end());
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

		//	Adding Supers and Interfaces
		for (String key : classes.keySet()) {
			if (classes.get(key) instanceof CWClass) {
				CWClass cwClass = (CWClass) classes.get(key);

				if (extendedClasses.containsKey(cwClass)) {
					String superName = extendedClasses.get(cwClass);
					if (classImports.get(cwClass).containsKey(superName)) {
						CWClass _super = (CWClass) classes.get(classImports.get(cwClass).get(extendedClasses.get(cwClass)));
						cwClass.setSuper(_super);
					} else {
						for (String _import : wildcardImports.get(cwClass)) {
							String importClass = _import + extendedClasses.get(cwClass);
							try {
								Class superClass = Class.forName(importClass);
								cwClass.setSuper(new CWExternalClass(superClass,extendedClasses.get(cwClass)));
								break;
							} catch (ClassNotFoundException ignored) {
							}
						}
					}
				}
				System.out.println(cwClass);
			}
		}


		return classes;
	}

	private interface test extends CWType, Extendable{}
}