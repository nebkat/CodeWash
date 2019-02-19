package ws.codewash;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import ws.codewash.parser.ParsedSourceTree;
import ws.codewash.parser.Parser;
import ws.codewash.reader.FolderReader;
import ws.codewash.util.Arguments;

import java.io.IOException;
import java.nio.file.Paths;

public class CodeWash {
	public static void main(String[] args) throws IOException {
		JCommander commander = JCommander.newBuilder()
				.addObject(Arguments.get())
				.build();

		try {
			commander.parse(args);
			if (Arguments.get().getConfigGenPath() != null) {
				//Todo: Generate default config
			}
			else if (Arguments.get().getSrcPath() != null) {
				System.out.println("Washing: " + Arguments.get().getSrcPath());
				FolderReader folderReader = new FolderReader(Paths.get(Arguments.get().getSrcPath()));
				ParsedSourceTree parsedSourceTree = new Parser().parse(folderReader.getSources());
			} else {
				//Todo: Create http server
			}
		} catch (ParameterException ignored) {
			commander.usage();
		}
	}
}
