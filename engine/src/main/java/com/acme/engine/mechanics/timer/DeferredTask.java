package com.acme.engine.mechanics.timer;

import com.acme.engine.mechanics.promises.Deferred;
import com.acme.engine.mechanics.promises.PromiseHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

class DeferredTask<T> extends Deferred<T, Throwable> implements PromiseTask<T> {

    final Callable<T> task;
    float atAge;
    float age;
    final float period;
    boolean cancelled;

    private final List<PromiseHandler<Void>> cancelledListeners = new ArrayList<>();

    DeferredTask(Callable<T> task, float delay, float period) {
        this.task = task;
        this.atAge += delay;
        this.period = period;
    }

    @Override
    public PromiseTask<T> cancel(PromiseHandler<Void> cancelHandler) {
        if (isCancelled()) {
            notifyCancelled(cancelHandler);
        } else {
            cancelledListeners.add(cancelHandler);
        }
        return this;
    }

    private static void notifyCancelled(PromiseHandler<Void> cancelHandler) {
        cancelHandler.handle(null);
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
        if (!isPending()) {
            throw new IllegalStateException();
        }
        cancelled = true;
        notifyCancelled();
    }

    private void notifyCancelled() {
        for (PromiseHandler<Void> handler : cancelledListeners) {
            handler.handle(null);
        }
        cancelledListeners.clear();
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}
