package ws.codewash;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ws.codewash.analyzer.Analyzer;
import ws.codewash.analyzer.reports.Report;
import ws.codewash.analyzer.result.Result;
import ws.codewash.http.ServerHandler;
import ws.codewash.java.ParsedSourceTree;
import ws.codewash.parser.Parser;
import ws.codewash.parser.grammar.Grammar;
import ws.codewash.reader.FolderReader;
import ws.codewash.reader.SourceReadable;
import ws.codewash.reader.ZipReader;
import ws.codewash.util.Arguments;
import ws.codewash.util.config.ConfigManager;
import ws.codewash.util.Log;
import ws.codewash.util.ReportWriter;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class CodeWash {
	private static final String TAG = "CodeWash";

	public static void main(String[] args) throws IOException {
		JCommander commander = JCommander.newBuilder()
				.addObject(Arguments.get())
				.build();

		ConfigManager configManager = ConfigManager.get();
		try {
			commander.parse(args);
			if (Arguments.get().getConfigGenPath() != null) {
				configManager.generateDefaultConfig(Arguments.get().getConfigGenPath());
			} else if (Arguments.get().getSrcPath() != null) {
				if (Arguments.get().getConfigPath() != null) {
					configManager.setConfigFile(Arguments.get().getConfigPath());
				}

				Log.i(TAG, "Washing: " + Arguments.get().getSrcPath());
				SourceReadable sources;
				if (Arguments.get().getSrcPath().endsWith(".zip") || Arguments.get().getSrcPath().endsWith(".jar")) {
					sources = new ZipReader(Paths.get(Arguments.get().getSrcPath()));
				} else {
					sources = new FolderReader(Paths.get(Arguments.get().getSrcPath()));
				}

				try {
					FileSystem fs = sources.getFileSystem();
					List<Path> files = Files.walk(fs.getPath(sources.getRootPath()))
							.filter(Files::isRegularFile)
							.collect(Collectors.toList());
					Grammar grammar = Grammar.parse(Paths.get("resources/language/java-11.cwls"));
					Log.i(TAG, "Beginning Parser.\n");
					ParsedSourceTree tree = new Parser(grammar).parse(files);
					Log.i(TAG, "Beginning Analyzer.\n");
					List<Report> reports = new Analyzer(tree).analyse();
					for (Report r : reports) {
						Log.i(TAG, r.toString());
					}
					Log.i(TAG, "Total number of smells detected: " + reports.size());
				} finally {
					try {
						sources.close();
					} catch (Exception e) {
						// Ignore
					}
				}

			} else {
				ServerHandler.init();
			}
		} catch (ParameterException ignored) {
			commander.usage();
		}
	}
}
