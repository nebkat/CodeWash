package ws.codewash.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import ws.codewash.CodeWash;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

		try {
			Files.write(filePath, GSON.toJson(Config.get()).getBytes());
		} catch (IOException ignored) {
			Log.e(TAG, "Unable to write to file: " + path);
		}
	}

	public static ConfigManager get() {
		return INSTANCE;
	}
}
