package ws.codewash.util;

public class Timing {
	public static long time() {
		return System.currentTimeMillis();
	}

	public static double duration(long start, long end) {
		return (end - start) / 1000.0;
	}
}
