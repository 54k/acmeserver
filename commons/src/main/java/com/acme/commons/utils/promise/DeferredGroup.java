package com.acme.commons.utils.promise;

import java.util.Collection;

final class DeferredGroup extends Deferred<Void, Void> {

	private final int size;
	private int resolved;
	private int rejected;

	@SuppressWarnings("unchecked")
	DeferredGroup(Collection<Promise<?, ?>> promises) {
		size = promises.size();
		for (Promise<?, ?> promise : promises) {
			promise.done(new PromiseHandler() {
				@Override
				public void handle(Object result) {
					resolved++;
					tryFulfill();
				}
			}).fail(new PromiseHandler() {
				@Override
				public void handle(Object result) {
					rejected++;
					tryFulfill();
				}
			});
		}
	}

	private void tryFulfill() {
		if (resolved + rejected < size) {
			return;
		}
		if (resolved == size) {
			resolve(null);
		} else {
			reject(null);
		}
	}
}
