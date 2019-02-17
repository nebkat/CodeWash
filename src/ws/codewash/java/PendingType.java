package ws.codewash.java;

import java.util.function.Consumer;

public class PendingType<T extends CWReferenceType> {
	private String mCanonicalName;
	private Consumer<T> mConsumer;

	PendingType(String canonicalName, Consumer<T> consumer) {
		mCanonicalName = canonicalName;
		mConsumer = consumer;
	}

	public String getCanonicalName() {
		return mCanonicalName;
	}

	public void accept(T type) {
		mConsumer.accept(type);
	}
}
