package ws.codewash.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import ws.codewash.CodeWash;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigManager {
	private static final String TAG = "ConfigManager";

	private static final ConfigManager INSTANCE = new ConfigManager();
	private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private ConfigManager() {
		JsonReader r = new JsonReader(new BufferedReader(
				new InputStreamReader(CodeWash.class.getResourceAsStream("../../config/DefaultConfig.json"))));
		Config.init(GSON.fromJson(r, Config.class));

		Log.d(TAG,"Default Config Loaded: \n"+GSON.toJson(Config.get()));
	}

	public void generateDefaultConfig(String path) {
		if (!path.endsWith(".json")) {
			path += "/CodeWash-Config.json";
		}
		Path filePath = Paths.get(path);

		Log.i(TAG, "Writing Config File to: \"" + path + "\"");
		try {
			Files.write(filePath, GSON.toJson(Config.get()).getBytes());
			Log.i(TAG, "Config File written to: \"" + path + "\"");
		} catch (IOException ignored) {
			Log.e(TAG, "Unable to write to file: \"" + path + "\"");
		}
	}

	public void setConfigFile(String path) {
		Log.d(TAG, "Loading Config File from: \"" + path + "\"");
		if (path.endsWith(".json")) {
			try {
				JsonReader r = new JsonReader(new FileReader(new File(path)));
				Config.set(GSON.fromJson(r, Config.class));
				Log.i(TAG, "Loaded Config File from: \"" + path + "\"");
			} catch (FileNotFoundException e) {
				Log.e(TAG, "Error finding Config file: \"" + path + "\" - Using default config.");
			}
		} else {
			Log.e(TAG, "Error finding Config file: \"" + path + "\" - Using default config.");
		}
	}

	public static ConfigManager get() {
		return INSTANCE;
	}
}
