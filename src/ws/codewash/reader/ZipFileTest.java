package ws.codewash.reader;

import ws.codewash.parser.Parser;

import java.nio.file.Paths;

public class ZipFileTest {
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("File name argument required");
			return;
		}

		ZipReader zipReader = new ZipReader(Paths.get(args[0]));

		new Parser().parse(zipReader.getSources());
	}
}
