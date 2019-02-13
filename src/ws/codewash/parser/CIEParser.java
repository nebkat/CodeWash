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

	private final Pattern mEnumPattern = Pattern.compile("\\s*enum\\s+(?<name>[a-zA-Z_][a-zA-Z0-9_]*)\\s*+" +
			"(\\+implements\\s+(?<interface>[a-zA-Z_][a-zA-Z0-9_]*\\s*(?:,\\s*[a-zA-Z_][a-zA-Z0-9_]*)*))?\\s*");

	private Map<String, CWAbstractClass> mClasses = new HashMap<>();
	private Map<CWAbstractClass, List<String>> mWildcardImports = new HashMap<>();
	private Map<CWAbstractClass, String> mExtendedClasses = new HashMap<>();
	private Map<CWAbstractClass, List<String>> mInterfacedClasses = new HashMap<>();
	private Stack<CWAbstractClass> mParentClasses = new Stack<>();
	private CWSourceTree mSourceTree;

	Map<String, CWAbstractClass> parseAbstractClass(CWSourceTree sourceTree, Map<Source, String> sources) {
		mSourceTree = sourceTree;

		//	Creating Classes and Interfaces
		findAbstractClass(sources);

		//	Adding Supers and Interfaces
		setSuperAndImplements();

		System.out.println("Amount of AbstractClasses: " + mClasses.values().size() + "\n");

		for (CWAbstractClass c : mClasses.values()) {
			System.out.println(c);
		}

		return mClasses;
	}

	private void findAbstractClass(Map<Source, String> sources) {
		for (Source s : sources.keySet()) {
		    while (!mParentClasses.empty()) {
		        mParentClasses.pop();
            }
			CWPackage cwPackage = null;
			ModifierHolder modifierHolder = new ModifierHolder();

			String source = sources.get(s);
			Matcher packageMatcher = mPackagePattern.matcher(source);
			Matcher classMatcher = mClassPattern.matcher(source);
			Matcher interfaceMatcher = mInterfacePattern.matcher(source);
			Matcher enumMatcher = mEnumPattern.matcher(source);
			Matcher modifierMatcher = mModifierPattern.matcher(source);

			Map<String, String> imports = new HashMap<>();
            List<String> wildcards = new ArrayList<>(Arrays.asList(mDefaultImports));

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

			while (packageMatcher.find()) {
				if (packageMatcher.start() == 0) {
					if (cwPackage == null) {
						cwPackage = mSourceTree.getPackages().get(packageMatcher.group(Keywords.PACKAGE));
						wildcards.add(cwPackage.getName() + ".");
					} else {
						System.err.println("Error parsing " + s.getName() + " | Package statement already declared..");
						break;
					}
				} else {
					System.err.println("Error parsing " + s.getName() + " | Package statement not at top.");
				}
			}

			while (source.length() > 0) {
				boolean classFound = classMatcher.find(), interfaceFound = interfaceMatcher.find(), enumFound = enumMatcher.find();
				int openIndex = Integer.MAX_VALUE, closeIndex = Integer.MAX_VALUE;

				if (classFound || interfaceFound || enumFound) {
				    int min = Integer.MAX_VALUE;
				    if (source.contains(";")) {
				        min = Integer.min(min, source.indexOf(";"));
                    }
                    if (source.contains("{")) {
                        min = Integer.min(min, openIndex = source.indexOf("{"));
                    }
                    if (source.contains("}")) {
                        min = Integer.min(min, closeIndex = source.indexOf("}"));
                    }
                    if (classFound) {
                        min = Integer.min(min, classMatcher.start());
                    }
                    if (interfaceFound) {
                        min = Integer.min(min, interfaceMatcher.start());
                    }
                    if (enumFound) {
                        min = Integer.min(min, enumMatcher.start());
                    }

                    if (modifierMatcher.find() && modifierMatcher.start() < min) {
                        modifierHolder.parse(modifierMatcher.group().replaceAll("\\s",""));
                        source = source.substring(modifierMatcher.end());
                    } else if (classFound && classMatcher.start() == min) {
                        addAbstractClass(classMatcher, Keywords.CLASS, modifierHolder, cwPackage, imports, wildcards);
                        modifierHolder.reset();
                        source = source.substring(classMatcher.end() + 1);

                    } else if (interfaceFound && interfaceMatcher.start() == min) {
                        addAbstractClass(interfaceMatcher, Keywords.INTERFACE, modifierHolder, cwPackage, imports, wildcards);
                        modifierHolder.reset();
                        source = source.substring(interfaceMatcher.end() + 1);

                    } else if (enumFound && enumMatcher.start() == min) {
                        addAbstractClass(enumMatcher, Keywords.ENUM, modifierHolder, cwPackage, imports, wildcards);
                        modifierHolder.reset();
                        source = source.substring(enumMatcher.end() + 1);
                    } else if (openIndex == min) {
                        mParentClasses.push(null);
                        modifierHolder.reset();
                        source = source.substring(min + 1);
                    } else if (closeIndex == min) {
                        mParentClasses.pop();
                        modifierHolder.reset();
                        source = source.substring(min + 1);
                    } else {
                        modifierHolder.reset();
                        source = source.substring(min + 1);
                    }
                } else {
					source = "";
				}

				classMatcher.reset(source);
				interfaceMatcher.reset(source);
				enumMatcher.reset(source);
				modifierMatcher.reset(source);
			}
		}
	}

	private void addAbstractClass(Matcher matcher, String type, ModifierHolder modifier, CWPackage cwPackage,
								  Map<String, String> imports, List<String> wildcards) {
		String name = matcher.group("name");
		if (!mParentClasses.empty()) {
		    name = mParentClasses.peek() != null ? mParentClasses.peek().getName() + "." + name : name;
        }
		String fullName = cwPackage == null ? name : cwPackage.getName() + "." + name;

		CWAbstractClass abstractClass = null;
		switch (type) {
			case Keywords.CLASS:
				mClasses.put(fullName, abstractClass = new CWClass(cwPackage, modifier.getAccessModifier(),
						modifier.isFinal(), modifier.isAbstract(), modifier.isStatic(), name));
				break;
			case Keywords.INTERFACE:
				mClasses.put(fullName, abstractClass = new CWInterface(cwPackage, modifier.getAccessModifier(),
						modifier.isFinal(), name));
				break;
			case Keywords.ENUM:
				mClasses.put(fullName, abstractClass = new CWEnum(cwPackage, modifier.getAccessModifier(), name));
				break;
		}

		if (abstractClass != null) {
		    mParentClasses.push(abstractClass);
			mClassImports.put(mClasses.get(fullName), imports);
			mWildcardImports.put(mClasses.get(fullName), wildcards);

			if (type.equals(Keywords.CLASS) && matcher.group(Keywords.SUPER) != null) {
				mExtendedClasses.put(mClasses.get(fullName), matcher.group(Keywords.SUPER));
			}

			if (matcher.group(Keywords.INTERFACE) != null) {
			    mInterfacedClasses.put(mClasses.get(fullName),
						Arrays.asList(matcher.group(Keywords.INTERFACE).split("(,\\s*)|(\\s+)")));
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
                    mClassImports.get(cwClass).put(superName,superClass.getCanonicalName());
					mSourceTree.addExternalClass(superName,extClass);
				} catch (ClassNotFoundException ignored) {}
			} else {
				for (String _import : mWildcardImports.get(cwClass)) {
					String importClass = _import + mExtendedClasses.get(cwClass);
					try {
						Class superClass = Class.forName(importClass);
						CWExternalClass extClass = new CWExternalClass(superClass, mExtendedClasses.get(cwClass));
						cwClass.setSuper(extClass);
                        mClassImports.get(cwClass).put(superName,superClass.getCanonicalName());
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
			mClassImports.get(aClass).put(interfaceName,extClass.getCanonicalName());
			mSourceTree.addExternalInterface(extClass.getCanonicalName(), extInterface);
			return true;
		} catch (ClassNotFoundException ignored) {
			return false;
		}
	}
}