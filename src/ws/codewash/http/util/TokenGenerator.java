package ws.codewash.http.util;

import ws.codewash.util.Log;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class TokenGenerator {
	private static final TokenGenerator INSTANCE = new TokenGenerator();
	private static final int TOKEN_LENGTH = 8;

	private Set<String> mTokens;
	private Random mRandom;

	private TokenGenerator(){
		mTokens = new HashSet<>();
		mRandom = new Random();
	}

	public static String getNextToken() {
		return INSTANCE.nextToken();
	}

	private String nextToken() {
		StringBuilder token = new StringBuilder();
		for (int i = 0 ; i < TOKEN_LENGTH; i++) {
			int rand = mRandom.nextInt(2);
			if (rand == 0) {
				token.append(Character.toString(mRandom.nextInt(9) + '0'));
			} else {
				token.append(Character.toString(mRandom.nextInt(26) + 'A'));
			}

		}

		if (mTokens.contains(token.toString()))
			return nextToken();

		mTokens.add(token.toString());

		return token.toString();
	}
}
