package ws.codewash.parser;

import ws.codewash.java.CWAbstractClass;
import ws.codewash.java.CWSourceTree;
import ws.codewash.reader.Source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class Parser {
    protected final String[] mDefaultImports = {"java.lang.","java.lang.annotation.","java.lang.instrument.",
            "java.lang.invoke.","java.lang.management.","java.lang.ref.","java.lang.reflect."};

	protected final Pattern mPackagePattern = Pattern.compile("\\s*package\\s+(?<"+Keywords.PACKAGE+">[a-zA-Z_][a-zA-Z0-9_]*(?:\\.[a-zA-Z_][a-zA-Z_0-9]*)*)\\s*;");
	protected final Pattern mImportPattern = Pattern.compile("\\s*import\\s+(?<"+Keywords.PACKAGE+">[a-zA-Z_][a-zA-Z0-9_]*(?:\\.[a-zA-Z_][a-zA-Z_0-9]*)*(\\.\\*)?)\\s*;");
	protected final Pattern mModifierPattern = Pattern.compile("\\s*(public|protected|private|abstract|static|final)\\s");

	protected final Map<CWAbstractClass, Map<String,String>> mClassImports = new HashMap<>();

    public static CWSourceTree parse(List<Source> sources) {
		CWSourceTree cb = new CWSourceTree();

		CommentParser c = new CommentParser();
		Map<Source, String> cSources = c.parseComments(sources);

		PackageParser packageParser = new PackageParser();
		cb.setPackages(packageParser.parsePackages(cSources));

		CIEParser cieParser = new CIEParser();
		cb.setAbstractClasses(cieParser.parseAbstractClass(cb,cSources));

        return cb;
    }

    final static public class Keywords {
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

        public static final String PUBLIC = "public";
        public static final String PRIVATE = "private";
        public static final String PROTECTED = "protected";
    }

}

