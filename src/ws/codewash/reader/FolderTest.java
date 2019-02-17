package ws.codewash.reader;

import ws.codewash.analyzer.Analyzer;
import ws.codewash.parser.ParsedSourceTree;
import ws.codewash.parser.Parser;

import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FolderTest {
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("File name argument required");
			return;
		}

		FolderReader folderReader = new FolderReader(Paths.get(args[0]));

		ParsedSourceTree parsedSourceTree = new Parser().parse(folderReader.getSources());
		List<String> selectedSmells = new ArrayList<>(){{
			add("ExcessiveCommenting");
			add("RefusedBequest");
		}};
		new Analyzer(selectedSmells, parsedSourceTree).analyse();
	}
}
