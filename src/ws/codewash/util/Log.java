package ws.codewash.util;

public class Log {
	// Color Reset
	private static final String RESET = "\033[0m";

	// Regular Colors
	private static final String BLACK = "\033[0;30m";
	private static final String RED = "\033[0;31m";
	private static final String GREEN = "\033[0;32m";
	private static final String YELLOW = "\033[0;33m";
	private static final String BLUE = "\033[0;34m";
	private static final String PURPLE = "\033[0;35m";
	private static final String CYAN = "\033[0;36m";
	private static final String WHITE = "\033[0;37m";

	// Bold
	private static final String BLACK_BOLD = "\033[1;30m";
	private static final String RED_BOLD = "\033[1;31m";
	private static final String GREEN_BOLD = "\033[1;32m";
	private static final String YELLOW_BOLD = "\033[1;33m";
	private static final String BLUE_BOLD = "\033[1;34m";
	private static final String PURPLE_BOLD = "\033[1;35m";
	private static final String CYAN_BOLD = "\033[1;36m";
	private static final String WHITE_BOLD = "\033[1;37m";

	public static void v(String TAG, String message) {
		if (true || Arguments.get().verbose()) {
			System.out.println(WHITE_BOLD + "VERBOSE/" + TAG + " : " + WHITE + message + RESET);
		}
	}

	public static void d(String TAG, String message) {
		if (true || Arguments.get().verbose()) {
			System.out.println(WHITE_BOLD + "DEBUG/" + TAG + " : " + WHITE + message + RESET);
		}
	}

	public static void e(String TAG, String message) {
		System.out.println(RED_BOLD + "ERROR/" + TAG + " : " + RED + message + RESET);
	}

	public static void i(String TAG, String message) {
		System.out.println(BLUE_BOLD + "INFO/" + TAG + " : " + BLUE + message + RESET);
	}

	public static void w(String TAG, String message) {
		System.out.println(YELLOW_BOLD + "WARN/" + TAG + " : " + YELLOW + message + RESET);
	}

	public static void wtf(String TAG, String message) {
		System.out.println(PURPLE_BOLD + "WTF/" + TAG + " : " + PURPLE + message + RESET);
	}
}
