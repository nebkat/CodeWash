package ws.codewash.analyzer;

import ws.codewash.analyzer.smells.CodeSmell;
import ws.codewash.parser.ParsedSourceTree;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class Analyzer {

	private List<CodeSmell> mCodeSmells = new ArrayList<>();

	public Analyzer(List<String> mSelectedSmells, ParsedSourceTree parsedSourceTree) {
		try {
			for (String o : mSelectedSmells) {
				Class<?> cls = Class.forName("ws.codewash.analyzer.smells." + o);
				Constructor cons = cls.getConstructor(String.class, ParsedSourceTree.class);
				mCodeSmells.add((CodeSmell) cons.newInstance(o, parsedSourceTree));

				System.out.println("Created new instance of " + o);

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			System.out.println("Number of smells to test for: " + mCodeSmells.size());
		}
	}

	public void analyse() {
		mCodeSmells.parallelStream().forEach(CodeSmell::run);
	}

}
