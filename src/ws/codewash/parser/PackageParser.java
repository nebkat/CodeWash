package ws.codewash.parser;

import ws.codewash.java.CWPackage;
import ws.codewash.reader.Source;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

public class PackageParser extends Parser {
    public Map<String, CWPackage> parsePackages(Map<Source, String> sources) {
        Map<String, CWPackage> packages = new HashMap<>();

        for (Source s : sources.keySet()) {
        	String source = sources.get(s);
        	Matcher packageMatcher = mPackagePattern.matcher(source);

			CWPackage cwPackage = null;

        	while (packageMatcher.find()) {
        		if (packageMatcher.start() == 0) {
					if (cwPackage == null) {
						String name = packageMatcher.group(Keywords.PACKAGE);
						String packageName = "";
						for (String pack : name.split("([.])")) {
							String parentPack = packageName;
							packageName += (packageName.length() == 0) ? pack : "." + pack;
							if (!packages.containsKey(packageName)) {
								cwPackage = new CWPackage(packageName);
								packages.put(packageName, cwPackage);
								if (packages.containsKey(parentPack)) {
									CWPackage parent = packages.get(parentPack);
									parent.addSubPackage(cwPackage);
									cwPackage.setContainer(parent);
								}
							}
						}
					} else {
						System.err.println("Error Parsing: " + s.getName() + " | More than one package declaration.");
					}
				} else {
					System.err.println("Error Parsing: " + s.getName() + " | Package declaration not at top.");
				}
			}
        }

        return packages;
    }
}
