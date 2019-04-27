package ws.codewash.http.util;

public class ByteSearch {
	public static int indexOf(byte[] data, byte[] pattern, int indexOf) {
		int[] failure = computeFailure(pattern);

		int currentIndex = 0;
		int j = 0;

		for (int i = 0; i < data.length; i++) {
			while (j > 0 && pattern[j] != data[i]) {
				j = failure[j - 1];
			}
			if (pattern[j] == data[i]) {
				j++;
			}
			if (j == pattern.length) {
				if (currentIndex != indexOf) {
					currentIndex++;
					j = 0;
					failure = computeFailure(pattern);
				} else
					return i - pattern.length + 1;
			}
		}
		return -1;
	}

	private static int[] computeFailure(byte[] pattern) {
		int[] failure = new int[pattern.length];

		int j = 0;
		for (int i = 1; i < pattern.length; i++) {
			while (j > 0 && pattern[j] != pattern[i]) {
				j = failure[j - 1];
			}
			if (pattern[j] == pattern[i]) {
				j++;
			}
			failure[i] = j;
		}

		return failure;
	}
}
