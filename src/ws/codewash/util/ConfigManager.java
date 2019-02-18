package ws.codewash.util;

import java.util.Properties;

public class ConfigManager {

	private static ConfigManager INSTANCE;

	private Properties mProperties = new Properties();

	private ConfigManager() {
		try{
			mProperties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config/CodeSmells.config"));
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}

	public static ConfigManager getInstance() {
		if (INSTANCE == null){
			INSTANCE = new ConfigManager();
		}
		return INSTANCE;
	}

	public Properties getProperties() {
		return mProperties;
	}

}
