package com.acme.engine.ecs.core;

import com.acme.engine.ecs.promise.Deferred;
import com.acme.engine.ecs.promise.Promise;
import com.acme.engine.ecs.promise.PromiseHandler;

import java.util.concurrent.Callable;

class DeferredTask<T> implements PromiseTask<T> {

    final Deferred<T, Throwable> deferred;
    final Callable<T> task;
    float atAge;
    float age;
    boolean cancelled;

    DeferredTask(Callable<T> task, float delay) {
        this.deferred = new Deferred<>();
        this.task = task;
        this.atAge += delay;
    }

    @Override
    public void cancel() {
        cancelled = true;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public Promise<T, Throwable> done(PromiseHandler<T> resolveHandler) {
        return deferred.done(resolveHandler);
    }

    @Override
    public Promise<T, Throwable> fail(PromiseHandler<Throwable> rejectHandler) {
        return deferred.fail(rejectHandler);
    }

    @Override
    public boolean isPending() {
        return deferred.isPending();
    }

    @Override
    public boolean isResolved() {
        return deferred.isResolved();
    }

    @Override
    public boolean isRejected() {
        return deferred.isRejected();
    }

    void execute() {
        try {
            T result = task.call();
            deferred.resolve(result);
        } catch (Throwable t) {
            deferred.reject(t);
        }
    }
}
