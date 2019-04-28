package ws.codewash.java;

import java.util.function.Consumer;

public class PendingType<T extends CWType> {
	private RawType mRawType;
	private Consumer<T> mConsumer;

	public PendingType(RawType rawType, Consumer<T> consumer) {
		mRawType = rawType;
		mConsumer = consumer;
	}

	public RawType getRawType() {
		return mRawType;
	}

	public void accept(T type) {
		mConsumer.accept(type);
	}
}
