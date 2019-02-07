package ws.codewash.parser;

import ws.codewash.java.CWPackage;
import ws.codewash.reader.Source;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PackageParser extends Parser {
    private final String regex;

    public PackageParser(String regex) {
        this.regex = regex;
    }

    public Map<String, CWPackage> parsePackages(Parsable sources) {
        Map<String, CWPackage> packages = new HashMap<>();
        boolean comment = false;
        boolean found;

        for (Source s : sources.getSources()) {
            found = false;
            Iterator<String> i = s.iterator();
            while (i.hasNext() && !found) {
                String line = i.next();

                if (requiredLine(line, comment)) {
                    String name = getPackageName(line);
                    String packageName = "";

                    for (String pack : name.split("([.])")) {
                        String parentPack = packageName;
                        packageName += (packageName.length() == 0) ? pack : "." + pack;
                        if (!packages.containsKey(packageName)) {
                            CWPackage cwPackage = new CWPackage(packageName);
                            packages.put(packageName, cwPackage);
                            if (packages.containsKey(parentPack)) {
                                CWPackage parent = packages.get(parentPack);
                                parent.addPackage(cwPackage);
                                cwPackage.setContainer(parent);
                            }
                        }
                    }
                    found = true;
                }

                if (!i.hasNext()) {
                    System.err.println("Couldn't parse package from: " + s.getName());
                }
            }
        }

        for (String s : packages.keySet()) {
            System.out.println(packages.get(s));
        }

        return packages;
    }

    private boolean requiredLine(String line, boolean comment) {
        if (line.isEmpty()) {
            return false;
        }

        Pattern packagePattern = Pattern.compile(regex);
        Matcher packageLine = packagePattern.matcher(line);

        Matcher commentMatcher;
        commentMatcher = comment ? CLOSE_COMMENT.matcher(line) : OPEN_COMMENT.matcher(line);

        Matcher lineCommentMatcher = LINE_COMMENT.matcher(line);
        if (lineCommentMatcher.find()) {
            if (lineCommentMatcher.start() == 0) {
                return false;
            }
        }

        if (comment) {
            if (commentMatcher.find()) {
                String subLine = line.substring(commentMatcher.start());
                return requiredLine(subLine, comment = false);
            }
            return false;
        } else {
            if (commentMatcher.find()) {
                String subLine = line.substring(commentMatcher.start());
                return requiredLine(subLine, comment = true);
            } else return packageLine.find();
        }
    }

    private String getPackageName(String line) {
        Pattern packagePattern = Pattern.compile("(" + Regex.PACKAGE_FORMATTING + "+[;])");
        Matcher packageMatcher = packagePattern.matcher(line);
        String name = "";
        if (packageMatcher.find()) {
            name = packageMatcher.group().substring(0,packageMatcher.group().indexOf(";"));
        }
        return name;
    }
}
