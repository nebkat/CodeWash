package ws.codewash.analyzer;

import ws.codewash.analyzer.smells.CodeSmell;
import ws.codewash.parser.ParsedSourceTree;
import ws.codewash.util.ConfigManager;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Analyzer {

	private List<CodeSmell> mCodeSmells = new ArrayList<>();

	public Analyzer(ParsedSourceTree parsedSourceTree) {
		try {
			Properties properties = ConfigManager.getInstance().getProperties();

			for (String s : properties.stringPropertyNames()) {
				if (Boolean.parseBoolean(properties.getProperty(s))) {
					Class<?> cls = Class.forName("ws.codewash.analyzer.smells." + s);
					Constructor cons = cls.getConstructor(String.class, ParsedSourceTree.class);
					mCodeSmells.add((CodeSmell) cons.newInstance(s, parsedSourceTree));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			System.out.println("Checking for the Following Smells:");
			for (CodeSmell codeSmell : mCodeSmells) {
				System.out.println("- " + codeSmell);
			}
		}
	}

	public void analyse() {
		mCodeSmells.parallelStream().forEach(CodeSmell::run);
	}

}
