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
		Map<CWAbstractClass, List<String>> interfacedClasses = new HashMap<>();

		for (Source s : sources.keySet()) {
			CWPackage cwPackage = null;
			CWAccessModifier accessModifier = null;
			boolean _static = false;
			boolean _final = false;
			boolean _abstract = false;

			String source = sources.get(s);
			Matcher packageMatcher = mPackagePattern.matcher(source);
			Matcher classMatcher = mClassPattern.matcher(source);
			Matcher interfaceMatcher = mInterfacePattern.matcher(source);
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
				if (packageMatcher.find() || classMatcher.find() || interfaceMatcher.find() || modifierMatcher.find()) {
					packageMatcher.reset();
					classMatcher.reset();
					modifierMatcher.reset();
					interfaceMatcher.reset();

					if (packageMatcher.find()) {
						if (packageMatcher.start() == 0) {
							if (cwPackage == null) {
								cwPackage = sourceTree.getPackages().get(packageMatcher.group(Keywords.PACKAGE));
								wildcards.add(cwPackage.getName() + ".");
								source = source.substring(packageMatcher.end());
							} else {
								System.err.println("Error parsing " + s.getName() + " | Package statement already declared..");
								break;
							}
						} else {
							System.err.println("Error parsing " + s.getName() + " | Package statement not at top.");
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
							interfacedClasses.put(classes.get(fullName),Arrays.asList(classMatcher.group(Keywords.INTERFACE).split("(,\\s*)|(\\s+)")));
						}

						accessModifier = null;
						_static = _final = _abstract = false;
						source = source.substring(classMatcher.end());

					} else if (interfaceMatcher.find() && interfaceMatcher.start() == 0) {
						String name = interfaceMatcher.group("name");
						String fullName = cwPackage == null ? name : cwPackage.getName() + "." + name;

						classes.put(fullName, new CWInterface(cwPackage, accessModifier, _final, name));
						classImports.put(classes.get(fullName), imports);
						wildcardImports.put(classes.get(fullName), wildcards);

						if (interfaceMatcher.group(Keywords.INTERFACE) != null) {
							interfacedClasses.put(classes.get(fullName),Arrays.asList(interfaceMatcher.group(Keywords.INTERFACE).split("(,\\s*)|(\\s+)")));
						}

						accessModifier = null;
						_static = _final = _abstract = false;
						source = source.substring(interfaceMatcher.end());

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
				interfaceMatcher.reset(source);
			}
		}

		//	Adding Supers and Interfaces
		for (String key : classes.keySet()) {
			if (classes.get(key) instanceof CWClass) {
				CWClass cwClass = (CWClass) classes.get(key);
				if (extendedClasses.containsKey(cwClass)) {
					String superName = extendedClasses.get(cwClass);
					if (classes.containsKey(superName)) {
						CWClass _super = (CWClass) classes.get(classImports.get(cwClass).get(extendedClasses.get(cwClass)));
						cwClass.setSuper(_super);
					} else if (sourceTree.getExtClasses().containsKey(superName)) {
						CWExternalClass extSuper = sourceTree.getExtClasses().get(superName);
						cwClass.setSuper(extSuper);
					} else {
						if (classImports.get(cwClass).containsKey(superName)) {
							try {
								Class superClass = Class.forName(superName);
								CWExternalClass extClass = new CWExternalClass(superClass, extendedClasses.get(cwClass));
								cwClass.setSuper(extClass);
								sourceTree.addExternalClass(superName,extClass);
							} catch (ClassNotFoundException ignored) {}
						} else {
							for (String _import : wildcardImports.get(cwClass)) {
								String importClass = _import + extendedClasses.get(cwClass);
								try {
									Class superClass = Class.forName(importClass);
									CWExternalClass extClass = new CWExternalClass(superClass, extendedClasses.get(cwClass));
									cwClass.setSuper(extClass);
									sourceTree.addExternalClass(superName,extClass);
									break;
								} catch (ClassNotFoundException ignored) {
								}
							}
						}
					}
				}

				if (interfacedClasses.containsKey(cwClass)) {
					for (String interfaceName : interfacedClasses.get(cwClass)) {
						addInterfaces(cwClass, interfaceName, classes, classImports.get(cwClass), wildcardImports.get(cwClass), sourceTree);
					}

				}
			} else if (classes.get(key) instanceof CWInterface) {
				CWInterface cwInterface = (CWInterface) classes.get(key);

				if (interfacedClasses.containsKey(cwInterface)) {
					for (String interfaceName : interfacedClasses.get(cwInterface)) {
						addInterfaces(cwInterface, interfaceName, classes, classImports.get(cwInterface), wildcardImports.get(cwInterface), sourceTree);
					}
				}
			}
			System.out.println(classes.get(key));
		}


		return classes;
	}

	private void addInterfaces(CWAbstractClass _class, String interfaceName, Map<String, CWAbstractClass> classes,
							   Map<String, String> classImports, List<String> wildcardImports, CWSourceTree tree) {
		String interfaceImport = classImports.get(interfaceName);
		if (classes.containsKey(interfaceImport)) {
			CWInterface cwInterface = (CWInterface) classes.get(classImports.get(interfaceName));
			if (_class instanceof CWClass) {
				((CWClass) _class).addInterface(cwInterface);
			} else if (_class instanceof CWInterface) {
				((CWInterface) _class).addInterface(cwInterface);
			}
		} else {
			if (classImports.containsKey(interfaceName)) {
				addExternalInterface(interfaceImport, interfaceName, _class, tree);
			} else {
				for (String _import : wildcardImports) {
					String importPackage = _import + interfaceName;
					if (classes.containsKey(importPackage)) {
						CWInterface cwInterface = (CWInterface) classes.get(importPackage);
						if (_class instanceof CWClass) {
							((CWClass) _class).addInterface(cwInterface);
						} else if (_class instanceof CWInterface) {
							((CWInterface) _class).addInterface(cwInterface);
						}
					} else if (tree.getExtInterfaces().containsKey(importPackage)) {
						CWExternalInterface cwInterface = tree.getExtInterfaces().get(importPackage);
						if (_class instanceof CWClass) {
							((CWClass) _class).addInterface(cwInterface);
						} else if (_class instanceof CWInterface) {
							((CWInterface) _class).addInterface(cwInterface);
						}
					} else {
						if (addExternalInterface(importPackage, interfaceName, _class, tree)) {
							break;
						}
					}
				}
			}
		}
	}

	private boolean addExternalInterface(String interfacePackage, String interfaceName, CWAbstractClass aClass, CWSourceTree tree) {
		try {
			Class extClass = Class.forName(interfacePackage);
			CWExternalInterface extInterface = new CWExternalInterface(extClass, interfaceName);
			if (aClass instanceof CWClass) {
				((CWClass) aClass).addInterface(extInterface);
			} else if (aClass instanceof CWInterface) {
				((CWInterface) aClass).addInterface(extInterface);
			}
			tree.addExternalInterface(extClass.getCanonicalName(), extInterface);
			return true;
		} catch (ClassNotFoundException ignored) {
			return false;
		}
	}
}