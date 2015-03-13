package com.acme.commons.utils.scheduler;

import com.acme.commons.utils.promise.Promise;
import com.acme.commons.utils.promise.PromiseHandler;

public interface PromiseTask<T> extends Promise<T, Throwable> {

    @Override
    PromiseTask<T> done(PromiseHandler<T> resolve);

    @Override
    PromiseTask<T> fail(PromiseHandler<Throwable> reject);

    /**
     * Rejects this task with {@link java.util.concurrent.CancellationException}
     */
    void cancel();

    boolean isCancelled();
}
