package ws.codewash.util.config;

import ws.codewash.analyzer.smells.CodeSmell;
import ws.codewash.analyzer.smells.bloatedcode.LongClasses;
import ws.codewash.analyzer.smells.bloatedcode.LongIDs;
import ws.codewash.analyzer.smells.bloatedcode.LongMethods;
import ws.codewash.analyzer.smells.bloatedcode.LongParameterList;
import ws.codewash.analyzer.smells.bloatedcode.PrimitiveObsession;
import ws.codewash.analyzer.smells.disposables.ArrowheadIndentation;
import ws.codewash.analyzer.smells.oopviolation.DataClass;
import ws.codewash.analyzer.smells.disposables.LazyClass;
import ws.codewash.analyzer.smells.disposables.TooManyLiterals;
import ws.codewash.analyzer.smells.oopviolation.DataHiding;
import ws.codewash.java.ParsedSourceTree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Config {
	private static Config sInstance = new Config();
	private static final Map<String, Function<ParsedSourceTree, CodeSmell>> CODE_SMELLS = new HashMap<>() {{
		put(LongClasses.NAME, LongClasses::new);
		put(LongIDs.NAME, LongIDs::new);
		put(LongMethods.NAME, LongMethods::new);
		put(LongParameterList.NAME, LongParameterList::new);
		put(PrimitiveObsession.NAME, PrimitiveObsession::new);
		put(ArrowheadIndentation.NAME, ArrowheadIndentation::new);
		put(LazyClass.NAME, LazyClass::new);
		put(TooManyLiterals.NAME, TooManyLiterals::new);
		put(DataClass.NAME, DataClass::new);
		put(DataHiding.NAME, DataHiding::new);
	}};

	public List<String> run;
	public SmellConfigs configs;

	public class SmellConfigs {
		public LongClasses.Config longClasses;
		public LongIDs.Config longIDs;
		public LongMethods.Config longMethods;
		public LongParameterList.Config longParameterList;
		public PrimitiveObsession.Config primitiveObsession;
		public ArrowheadIndentation.Config arrowheadIndentation;
		public LazyClass.Config lazyClass;
		public TooManyLiterals.Config tooManyLiterals;
		public DataClass.Config dataClass;
		public DataHiding.Config dataHiding;
	}

	/**
	 * Sets the {@link Config} to be a new specified parsed {@link Config}.
	 *
	 * @param config the new {@code Config} to be loaded.
	 */
	public static void set(Config config) {
		sInstance = config;
	}

	/**
	 * Getter for the {@link ConfigManager}.
	 *
	 * @return instance of the {@link ConfigManager}
	 */
	public static Config get() {
		return sInstance;
	}

	public List<Function<ParsedSourceTree, CodeSmell>> getSmells() {
		return run.stream()
				.map(CODE_SMELLS::get)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}
}
