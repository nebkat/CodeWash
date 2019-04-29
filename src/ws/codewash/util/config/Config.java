package ws.codewash.util.config;

import ws.codewash.analyzer.smells.CodeSmell;
import ws.codewash.analyzer.smells.bloatedcode.ArrowheadIndentation;
import ws.codewash.analyzer.smells.bloatedcode.LongClasses;
import ws.codewash.analyzer.smells.bloatedcode.LongIDs;
import ws.codewash.analyzer.smells.bloatedcode.LongMethods;
import ws.codewash.analyzer.smells.bloatedcode.LongParameterList;
import ws.codewash.analyzer.smells.bloatedcode.PrimitiveObsession;
import ws.codewash.analyzer.smells.oopviolation.DataClass;
import ws.codewash.java.ParsedSourceTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Config {
	private final static Config INSTANCE = new Config();
	private static final Map<String, Function<ParsedSourceTree, CodeSmell>> CODE_SMELLS = new HashMap<>() {{
		put(LongMethods.NAME, LongMethods::new);
		put(LongParameterList.NAME, LongParameterList::new);
		put(PrimitiveObsession.NAME, PrimitiveObsession::new);
		put(LongIDs.NAME, LongIDs::new);
		put(LongClasses.NAME, LongClasses::new);
		put(ArrowheadIndentation.NAME, ArrowheadIndentation::new);
		put(DataClass.NAME, DataClass::new);
	}};

	private final List<String> SelectedCodeSmells = new ArrayList<>();
	private final Map<String, Number> LongMethodsConfig = new HashMap<>();
	private final Map<String, Number> LongParametersListConfig = new HashMap<>();
	private final Map<String, Number> PrimitiveObsessionConfig = new HashMap<>();
	private final Map<String, Number> LongIDsConfig = new HashMap<>();
	private final Map<String, Number> LongClassConfig = new HashMap<>();
	private final Map<String, Number> ArrowheadIndentationConfig = new HashMap<>();

	/**
	 * Private constructor for singleton.
	 */
	private Config() {
	}

	/**
	 * Initialises the original config. Called from {@link ConfigManager} constructor.
	 *
	 * @param config the default config loaded.
	 */
	static void init(Config config) {
		INSTANCE.SelectedCodeSmells.addAll(config.SelectedCodeSmells);
		updateMaps(config);
	}

	/**
	 * Sets the {@link Config} to be a new specified parsed {@link Config}.
	 *
	 * @param config the new {@code Config} to be loaded.
	 */
	public static void set(Config config) {
		// Clear the current selected smells
		while (!INSTANCE.SelectedCodeSmells.isEmpty()) {
			INSTANCE.SelectedCodeSmells.remove(0);
		}

		// Populate selected smells with new ones
		INSTANCE.SelectedCodeSmells.addAll(config.SelectedCodeSmells);
		updateMaps(config);
	}

	/**
	 * Updates the current {@link Config} smells configurations with a new one pass through.
	 *
	 * @param config the new {@code Config} to be loaded.
	 */
	private static void updateMaps(Config config) {
		config.LongMethodsConfig.forEach(INSTANCE.LongMethodsConfig::put);
		config.LongParametersListConfig.forEach(INSTANCE.LongParametersListConfig::put);
		config.PrimitiveObsessionConfig.forEach(INSTANCE.PrimitiveObsessionConfig::put);
		config.LongIDsConfig.forEach(INSTANCE.LongIDsConfig::put);
		config.LongClassConfig.forEach(INSTANCE.LongClassConfig::put);
		config.ArrowheadIndentationConfig.forEach(INSTANCE.ArrowheadIndentationConfig::put);
	}

	/**
	 * Getter for the {@link ConfigManager}.
	 *
	 * @return instance of the {@link ConfigManager}
	 */
	public static Config get() {
		return INSTANCE;
	}

	public Map<String, Function<ParsedSourceTree, CodeSmell>> getSmells() {
		Map<String, Function<ParsedSourceTree, CodeSmell>> smells = new HashMap<>();
		for (String s : SelectedCodeSmells) {
			smells.put(s, CODE_SMELLS.get(s));
		}
		return smells;
	}

	public Number LongMethodsConfig(String config) {
		return LongMethodsConfig.get(config);
	}

	public Number LongParameterListConfig(String config) {
		return LongParametersListConfig.get(config);
	}

	public Number PrimitiveObsessionConfig(String config) {
		return PrimitiveObsessionConfig.get(config);
	}

	public Number LongIDsConfig(String config) {
		return LongIDsConfig.get(config);
	}

	public Number LongClassConfig(String config) {
		return LongClassConfig.get(config);
	}

	public Number ArrowheadConfig(String config) {
		return ArrowheadIndentationConfig.get(config);
	}
}
