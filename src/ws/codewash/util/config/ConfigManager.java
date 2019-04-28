package ws.codewash.util.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import ws.codewash.CodeWash;
import ws.codewash.util.Log;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Config Management singleton class for the {@link Config}.
 */
public class ConfigManager {
	private static final String TAG = "ConfigManager";

	private static final ConfigManager INSTANCE = new ConfigManager();
	private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	/**
	 * Private constructor for singleton.
	 */
	private ConfigManager() {
		JsonReader r = new JsonReader(new BufferedReader(
				new InputStreamReader(CodeWash.class.getResourceAsStream("../../config/DefaultConfig.json"))));
		Config.init(GSON.fromJson(r, Config.class));

		Log.d(TAG, "Default Config Loaded: \n" + GSON.toJson(Config.get()));
	}

	/**
	 * Getter for the {@link ConfigManager}.
	 *
	 * @return instance of the {@link ConfigManager}
	 */
	public static ConfigManager get() {
		return INSTANCE;
	}

	/**
	 * Generates a JSon file of the embedded default config to a location.
	 * This is for the user to be able set their own configuration for the smells.
	 *
	 * @param path to the location for the config to be generated.
	 */
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

	/**
	 * Sets the {@link Config} to be a specific JSon from a location.
	 *
	 * @param path to the location the {@link Config} will be loaded from.
	 */
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
}
