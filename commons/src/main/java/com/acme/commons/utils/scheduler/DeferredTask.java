package com.acme.commons.utils.scheduler;

import com.acme.commons.utils.promise.Deferred;
import com.acme.commons.utils.promise.PromiseHandler;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;

final class DeferredTask<T> extends Deferred<T, Throwable> implements PromiseTask<T> {

	private final Callable<T> task;
	private float atAge;
	private float age;
	private final float period;
	private final Queue<DeferredTask<?>> taskQueue;

	private boolean cancelled;

	DeferredTask(Callable<T> task, float delay, float period, Queue<DeferredTask<?>> taskQueue) {
		this.task = task;
		this.atAge += delay;
		this.period = period;
		this.taskQueue = taskQueue;
	}

	void run(float deltaTime) {
		if (cancelled) {
			return;
		}

		age += deltaTime;
		if (age < atAge) {
			taskQueue.add(this);
			return;
		}

		try {
			T result = task.call();
			if (period > -1) {
				atAge += period;
				taskQueue.add(this);
			} else {
				resolve(result);
			}
		} catch (Throwable t) {
			reject(t);
		}
	}

	@Override
	public PromiseTask<T> done(PromiseHandler<T> resolve) {
		super.done(resolve);
		return this;
	}

	@Override
	public PromiseTask<T> fail(PromiseHandler<Throwable> reject) {
		super.fail(reject);
		return this;
	}

	@Override
	public void cancel() {
		checkFulfilled();
		cancelled = true;
		reject(new CancellationException());
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}
}
