package ws.codewash.reader;

import ws.codewash.parser.Parser;

import java.net.URI;
import java.nio.file.Paths;

public class FolderTest {
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("File name argument required");
			return;
		}

		FolderReader folderReader = new FolderReader(Paths.get(args[0]));

		new Parser().parse(folderReader.getSources());
	}
}
