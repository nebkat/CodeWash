package ws.codewash;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import ws.codewash.parser.ParsedSourceTree;
import ws.codewash.parser.Parser;
import ws.codewash.reader.FolderReader;
import ws.codewash.reader.SourceReadable;
import ws.codewash.reader.ZipReader;
import ws.codewash.util.Arguments;
import ws.codewash.util.ConfigManager;
import ws.codewash.util.Log;

import java.io.IOException;
import java.nio.file.Paths;

public class CodeWash {
	private static final String TAG = "CODEWASH";

	public static void main(String[] args) throws IOException {
		JCommander commander = JCommander.newBuilder()
				.addObject(Arguments.get())
				.build();

		try {
			commander.parse(args);
			if (Arguments.get().getConfigGenPath() != null) {
				ConfigManager.get().generateDefaultConfig(Arguments.get().getConfigGenPath());
			} else if (Arguments.get().getSrcPath() != null) {
				if (Arguments.get().getConfigPath() != null) {
					ConfigManager.get().setConfigFile(Arguments.get().getConfigPath());
				}

				Log.i(TAG,"Washing: " + Arguments.get().getSrcPath());
				SourceReadable sources;
				if (Arguments.get().getSrcPath().endsWith(".zip")) {
					sources = new ZipReader(Paths.get(Arguments.get().getSrcPath()));
				} else if (Arguments.get().getSrcPath().endsWith(".jar")) {
					//Todo: Jar reader
					sources = () -> null;
				} else {
					sources = new FolderReader(Paths.get(Arguments.get().getSrcPath()));
				}
				ParsedSourceTree parsedSourceTree = new Parser().parse(sources.getSources());

			} else {
				//Todo: Create http server
			}
		} catch (ParameterException ignored) {
			commander.usage();
		}
	}
}
