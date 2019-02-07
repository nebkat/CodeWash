package ws.codewash.parser;

import ws.codewash.java.CWSourceTree;

import java.util.regex.Pattern;

public abstract class Parser {
    protected static final Pattern OPEN_COMMENT = Pattern.compile("/\\*");
    protected static final Pattern CLOSE_COMMENT = Pattern.compile("\\*/");
    protected static final Pattern LINE_COMMENT = Pattern.compile("(" + Regex.WHITE_SPACE + "*//)");

    public static CWSourceTree parse (Parsable javaDirectory) {
        PackageParser packageParser = new PackageParser("(" + Regex.WHITE_SPACE + "*" + Keywords.PACKAGE + "+" + Regex.WHITE_SPACE + "+" + Regex.PACKAGE_FORMATTING + "+[;])");
        packageParser.parsePackages(javaDirectory);

        CWSourceTree cb = new CWSourceTree();
        return cb;
    }

    public static class Keywords {
        public static final String ABSTRACT = "abstract";
        public static final String ASSERT = "assert";
        public static final String BREAK = "break";
        public static final String CASE = "case";
        public static final String CATCH = "catch";
        public static final String CHAR = "char";
        public static final String CLASS = "class";
        public static final String CONTINUE = "continue";
        public static final String DEFAULT = "default";
        public static final String DO = "do";
        public static final String ELSE = "else";
        public static final String ENUM = "enum";
        public static final String EXTENDS = "extends";
        public static final String FINAL = "final";
        public static final String FINALLY = "finally";
        public static final String FOR = "for";
        public static final String IF = "if";
        public static final String IMPLEMENTS = "implements";
        public static final String IMPORT = "import";
        public static final String INSTANCEOF = "instance of";
        public static final String INTERFACE = "interface";
        public static final String NATIVE = "native";
        public static final String NEW = "new";
        public static final String PACKAGE = "package";
        public static final String RETURN = "return";
        public static final String STATIC = "static";
        public static final String SUPER = "super";
        public static final String SWITCH = "switch";
        public static final String SYNCHFRONIZED = "synchronized";
        public static final String THIS = "this";
        public static final String THROW = "throw";
        public static final String THROWS = "throws";
        public static final String TRANSIENT = "transient";
        public static final String TRY = "try";
        public static final String VOID = "void";
        public static final String VOLATILE = "volatile";
        public static final String WHILE = "while";
    }

    protected static class Regex {
        protected static final String WHITE_SPACE = "(\\s)";
        protected static final String PACKAGE_FORMATTING = "([a-zA-z0-9_.])";
    }
}
