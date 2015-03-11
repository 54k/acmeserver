package com.acme.commons.utils.scheduler;

import com.acme.commons.utils.promises.Deferred;
import com.acme.commons.utils.promises.PromiseHandler;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;

final class DeferredTask<T> extends Deferred<T, Throwable> implements PromiseTask<T> {

    final Callable<T> task;
    float atAge;
    float age;
    final float period;
    boolean cancelled;

    DeferredTask(Callable<T> task, float delay, float period) {
        this.task = task;
        this.atAge += delay;
        this.period = period;
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
