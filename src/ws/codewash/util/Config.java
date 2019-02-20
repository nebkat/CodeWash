package ws.codewash.util;

import ws.codewash.analyzer.smells.CodeSmell;
import ws.codewash.analyzer.smells.LongMethods;
import ws.codewash.analyzer.smells.LongParameterList;
import ws.codewash.analyzer.smells.PrimitiveObsession;
import ws.codewash.parser.ParsedSourceTree;

import java.util.*;
import java.util.function.Function;

public class Config {
	private final static Config INSTANCE = new Config();

	private Config(){}

	private static final Map<String, Function<ParsedSourceTree, CodeSmell>> CODE_SMELLS = new HashMap<>(){{
			put(LongMethods.NAME, LongMethods::new);
			put(LongParameterList.NAME, LongParameterList::new);
			put(PrimitiveObsession.NAME, PrimitiveObsession::new);
	}};

	private final List<String> SelectedCodeSmells = new ArrayList<>();

	private final Map<String, Number> LongMethodsConfig = new HashMap<>();
	private final Map<String, Number> LongParametersListConfig = new HashMap<>();
	private final Map<String, Number> PrimitiveObsessionConfig = new HashMap<>();

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
		for (String s : config.LongMethodsConfig.keySet()) {
			INSTANCE.LongMethodsConfig.put(s, config.LongMethodsConfig.get(s));
		}
		for (String s : config.LongParametersListConfig.keySet()) {
			INSTANCE.LongParametersListConfig.put(s, config.LongParametersListConfig.get(s));
		}
		for (String s : config.PrimitiveObsessionConfig.keySet()) {
			INSTANCE.PrimitiveObsessionConfig.put(s, config.PrimitiveObsessionConfig.get(s));
		}
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

}
