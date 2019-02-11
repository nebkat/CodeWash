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

	private Map<String, CWAbstractClass> mClasses = new HashMap<>();
	private Map<CWAbstractClass, Map<String,String>> mClassImports = new HashMap<>();
	private Map<CWAbstractClass, List<String>> mWildcardImports = new HashMap<>();
	private Map<CWAbstractClass, String> mExtendedClasses = new HashMap<>();
	private Map<CWAbstractClass, List<String>> mInterfacedClasses = new HashMap<>();
	private CWSourceTree mSourceTree;

	Map<String, CWAbstractClass> parseAbstractClass(CWSourceTree sourceTree, Map<Source, String> sources) {
		mSourceTree = sourceTree;

		//	Creating Classes and Interfaces
		createClassAndInterface(sources);

		//	Adding Supers and Interfaces
		setSuperAndImplements();

		return mClasses;
	}

	private void createClassAndInterface(Map<Source, String> sources) {
		for (Source s : sources.keySet()) {
			CWPackage cwPackage = null;
			ModifierHolder modifierHolder = new ModifierHolder();

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
								cwPackage = mSourceTree.getPackages().get(packageMatcher.group(Keywords.PACKAGE));
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

						mClasses.put(fullName, new CWClass(cwPackage, modifierHolder.getAccessModifier(),
								modifierHolder.isFinal(), modifierHolder.isAbstract(), modifierHolder.isStatic(), name));
						mClassImports.put(mClasses.get(fullName),imports);
						mWildcardImports.put(mClasses.get(fullName), wildcards);

						if (classMatcher.group(Keywords.SUPER) != null) {
							mExtendedClasses.put(mClasses.get(fullName), classMatcher.group(Keywords.SUPER));
						}
						if (classMatcher.group(Keywords.INTERFACE) != null) {
							mInterfacedClasses.put(mClasses.get(fullName),
									Arrays.asList(classMatcher.group(Keywords.INTERFACE).split("(,\\s*)|(\\s+)")));
						}

						modifierHolder.reset();
						source = source.substring(classMatcher.end());

					} else if (interfaceMatcher.find() && interfaceMatcher.start() == 0) {
						String name = interfaceMatcher.group("name");
						String fullName = cwPackage == null ? name : cwPackage.getName() + "." + name;

						mClasses.put(fullName, new CWInterface(cwPackage, modifierHolder.getAccessModifier(),
								modifierHolder.isFinal(), name));
						mClassImports.put(mClasses.get(fullName), imports);
						mWildcardImports.put(mClasses.get(fullName), wildcards);

						if (interfaceMatcher.group(Keywords.INTERFACE) != null) {
							mInterfacedClasses.put(mClasses.get(fullName),
									Arrays.asList(interfaceMatcher.group(Keywords.INTERFACE).split("(,\\s*)|(\\s+)")));
						}

						modifierHolder.reset();
						source = source.substring(interfaceMatcher.end());

					} else if (modifierMatcher.find()) {
						modifierHolder.parse(modifierMatcher.group().replaceAll("\\s",""));
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

	}

	private void setSuperAndImplements() {
		for (String key : mClasses.keySet()) {
			if (mClasses.get(key) instanceof CWClass) {
				CWClass cwClass = (CWClass) mClasses.get(key);
				if (mExtendedClasses.containsKey(cwClass)) {
					setSuper(cwClass, mExtendedClasses.get(cwClass));
				}
				if (mInterfacedClasses.containsKey(cwClass)) {
					for (String interfaceName : mInterfacedClasses.get(cwClass)) {
						addInterfaces(cwClass, interfaceName, mClasses, mClassImports.get(cwClass),
								mWildcardImports.get(cwClass));
					}

				}
			} else if (mClasses.get(key) instanceof CWInterface) {
				CWInterface cwInterface = (CWInterface) mClasses.get(key);

				if (mInterfacedClasses.containsKey(cwInterface)) {
					for (String interfaceName : mInterfacedClasses.get(cwInterface)) {
						addInterfaces(cwInterface, interfaceName, mClasses, mClassImports.get(cwInterface),
								mWildcardImports.get(cwInterface));
					}
				}
			}
		}
	}
	
	private void setSuper(CWClass cwClass, String superName) {
		if (mClasses.containsKey(superName)) {
			CWClass superClass = (CWClass) mClasses.get(mClassImports.get(cwClass).get(mExtendedClasses.get(cwClass)));
			cwClass.setSuper(superClass);
		} else if (mSourceTree.getExtClasses().containsKey(superName)) {
			CWExternalClass extSuper = mSourceTree.getExtClasses().get(superName);
			cwClass.setSuper(extSuper);
		} else {
			if (mClassImports.get(cwClass).containsKey(superName)) {
				try {
					Class superClass = Class.forName(superName);
					CWExternalClass extClass = new CWExternalClass(superClass, mExtendedClasses.get(cwClass));
					cwClass.setSuper(extClass);
					mSourceTree.addExternalClass(superName,extClass);
				} catch (ClassNotFoundException ignored) {}
			} else {
				for (String _import : mWildcardImports.get(cwClass)) {
					String importClass = _import + mExtendedClasses.get(cwClass);
					try {
						Class superClass = Class.forName(importClass);
						CWExternalClass extClass = new CWExternalClass(superClass, mExtendedClasses.get(cwClass));
						cwClass.setSuper(extClass);
						mSourceTree.addExternalClass(superName,extClass);
						break;
					} catch (ClassNotFoundException ignored) {
					}
				}
			}
		}
	}

	private void addInterfaces(CWAbstractClass _class, String interfaceName, Map<String, CWAbstractClass> classes,
							   Map<String, String> classImports, List<String> wildcardImports) {
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
				addExternalInterface(interfaceImport, interfaceName, _class);
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
					} else if (mSourceTree.getExtInterfaces().containsKey(importPackage)) {
						CWExternalInterface cwInterface = mSourceTree.getExtInterfaces().get(importPackage);
						if (_class instanceof CWClass) {
							((CWClass) _class).addInterface(cwInterface);
						} else if (_class instanceof CWInterface) {
							((CWInterface) _class).addInterface(cwInterface);
						}
					} else {
						if (addExternalInterface(importPackage, interfaceName, _class)) {
							break;
						}
					}
				}
			}
		}
	}

	private boolean addExternalInterface(String interfacePackage, String interfaceName, CWAbstractClass aClass) {
		try {
			Class extClass = Class.forName(interfacePackage);
			CWExternalInterface extInterface = new CWExternalInterface(extClass, interfaceName);
			if (aClass instanceof CWClass) {
				((CWClass) aClass).addInterface(extInterface);
			} else if (aClass instanceof CWInterface) {
				((CWInterface) aClass).addInterface(extInterface);
			}
			mSourceTree.addExternalInterface(extClass.getCanonicalName(), extInterface);
			return true;
		} catch (ClassNotFoundException ignored) {
			return false;
		}
	}
}