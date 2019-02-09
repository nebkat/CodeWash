package ws.codewash.parser;

import ws.codewash.java.CWPackage;
import ws.codewash.reader.Source;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;

public class PackageParser extends Parser {
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

        return packages;
    }
}
