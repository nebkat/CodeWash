package ws.codewash.util;

import ws.codewash.analyzer.smells.CodeSmell;
import ws.codewash.analyzer.smells.bloatedcode.LongIDs;
import ws.codewash.analyzer.smells.bloatedcode.LongMethods;
import ws.codewash.analyzer.smells.bloatedcode.LongParameterList;
import ws.codewash.analyzer.smells.bloatedcode.PrimitiveObsession;
import ws.codewash.parser.ParsedSourceTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Config {
	private final static Config INSTANCE = new Config();

	private Config() {
	}

	private static final Map<String, Function<ParsedSourceTree, CodeSmell>> CODE_SMELLS = new HashMap<>() {{
		put(LongMethods.NAME, LongMethods::new);
		put(LongParameterList.NAME, LongParameterList::new);
		put(PrimitiveObsession.NAME, PrimitiveObsession::new);
		put(LongIDs.NAME, LongIDs::new);
	}};

	private final List<String> SelectedCodeSmells = new ArrayList<>();

	private final Map<String, Number> LongMethodsConfig = new HashMap<>();
	private final Map<String, Number> LongParametersListConfig = new HashMap<>();
	private final Map<String, Number> PrimitiveObsessionConfig = new HashMap<>();
	private final Map<String, Number> LongIDsConfig = new HashMap<>();

	public static void init(Config config) {
		INSTANCE.SelectedCodeSmells.addAll(config.SelectedCodeSmells);
		updateMaps(config);
	}

	public static void set(Config config) {
		while (!INSTANCE.SelectedCodeSmells.isEmpty()) {
			INSTANCE.SelectedCodeSmells.remove(0);
		}
		INSTANCE.SelectedCodeSmells.addAll(config.SelectedCodeSmells);
		updateMaps(config);
	}

	private static void updateMaps(Config config) {
		config.LongMethodsConfig.forEach(INSTANCE.LongMethodsConfig::put);
		config.LongParametersListConfig.forEach(INSTANCE.LongParametersListConfig::put);
		config.PrimitiveObsessionConfig.forEach(INSTANCE.PrimitiveObsessionConfig::put);
		config.LongIDsConfig.forEach(INSTANCE.LongIDsConfig::put);
	}

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
}
