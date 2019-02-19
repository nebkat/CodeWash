package ws.codewash.util;

import com.beust.jcommander.Parameter;

public class Arguments {
	private static final Arguments INSTANCE = new Arguments();

	private Arguments() {}

	@Parameter(names = {"-p", "--port"}, description = "The port to bind the web-server.")
	private Integer mPort = 80;

	@Parameter(names = {"-b", "--bind", "--host"}, description = "The hostname/ip to bind the web-server")
	private String mHostname = "127.0.0.1";

	@Parameter(names = {"-s", "--strict"}, description = "Will terminate the program on error.")
	private Boolean mStrict = false;

	@Parameter(names = {"-v", "--verbose"}, description = "Enables verbose mode.")
	private Boolean mVerbose = false;

	@Parameter(names = {"-c", "--config"}, description = "Specifies the path to the config file.")
	private String mConfigPath = System.getProperty("user.dir");

	@Parameter(names = {"-gc", "--generate_config"}, description = "Generates a default config to a" +
			" specific path and terminates.")
	private String mConfigGenPath;

	@Parameter(names = {"wash"}, description = "Specifies the path to the source directory," +
			" runs the analyzer and terminates.")
	private String mSrcPath;

	@Parameter(names = {"-o, --output"}, description = "Specifies the path to generate the report.")
	private String mOutPath = System.getProperty("user.dir") + "\\out";

	@Parameter(names = {"-h, --help"}, description = "Displays this message and terminates.", help = true)
	private Boolean mHelp;

	public Integer getPort() {
		return mPort;
	}

	public String getHostname() {
		return mHostname;
	}

	public Boolean strict() {
		return mStrict;
	}

	public Boolean verbose() {
		return mVerbose;
	}

	public String getConfigPath() {
		return mConfigPath;
	}

	public String getConfigGenPath() {
		return mConfigGenPath;
	}

	public String getSrcPath() {
		return mSrcPath;
	}

	public String getOutPath() {
		return mOutPath;
	}

	public static Arguments get() {
		return INSTANCE;
	}

	@Override
	public String toString() {
		return "Hostname: " + mHostname + ":" + mPort + "\n" +
		"Verbose: " + mVerbose + "\n" +
		"Strict: " + mStrict + "\n" +
		"Config: " + mConfigPath+ "\n" +
		"Source: " + mSrcPath + "\n" +
		"Output: " + mOutPath + "\n";
	}
}
