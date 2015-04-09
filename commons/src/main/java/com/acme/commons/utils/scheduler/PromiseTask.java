package com.acme.commons.utils.scheduler;

import com.acme.commons.utils.promise.Promise;
import com.acme.commons.utils.promise.PromiseHandler;

public interface PromiseTask<T> extends Promise<T, Throwable> {

	@Override
	PromiseTask<T> done(PromiseHandler<T> resolveHandler);

	@Override
	PromiseTask<T> fail(PromiseHandler<Throwable> rejectHandler);

	/**
	 * Rejects this task with {@link java.util.concurrent.CancellationException}
	 */
	void cancel();

	/**
	 * Checks whether this promise is cancelled
	 *
	 * @return true if promise is cancelled, false otherwise
	 */
	boolean isCancelled();
}
