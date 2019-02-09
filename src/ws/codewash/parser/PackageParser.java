package ws.codewash.parser;

import ws.codewash.java.CWPackage;
import ws.codewash.reader.Source;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PackageParser extends Parser {
	private final Pattern mPackagePattern = Pattern.compile("\\s*package+\\s+(?<"+Keywords.PACKAGE+">[a-zA-Z_][a-zA-Z0-9_]*(?:.[a-zA-Z_][a-zA-Z_0-9]*)*)\\s*;");

    public Map<String, CWPackage> parsePackages(Map<Source, String> sources) {
        Map<String, CWPackage> packages = new HashMap<>();
        boolean found;

        for (Source s : sources.keySet()) {
			found = false;
			Scanner scanner = new Scanner(sources.get(s));

            while (scanner.hasNextLine() && !found) {
                String line = scanner.nextLine();
                Matcher packageLine = mPackagePattern.matcher(line);

                if (packageLine.find()) {
                    String name = packageLine.group(Keywords.PACKAGE);
                    String packageName = "";

                    for (String pack : name.split("([.])")) {
                        String parentPack = packageName;
                        packageName += (packageName.length() == 0) ? pack : "." + pack;
                        if (!packages.containsKey(packageName)) {
                            CWPackage cwPackage = new CWPackage(packageName);
                            packages.put(packageName, cwPackage);
                            if (packages.containsKey(parentPack)) {
                                CWPackage parent = packages.get(parentPack);
                                parent.addSubPackage(cwPackage);
                                cwPackage.setContainer(parent);
                            }
                        }
                    }
                    found = true;
                }
                if (!scanner.hasNextLine()) {
                    System.err.println("Couldn't parse package from: " + s.getName());
                }
            }
        }

        for (String s : packages.keySet()) {
            System.out.println(packages.get(s));
        }

        return packages;
    }
}
