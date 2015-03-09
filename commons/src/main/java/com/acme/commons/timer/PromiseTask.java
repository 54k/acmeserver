package com.acme.commons.timer;

import com.acme.commons.promises.Promise;
import com.acme.commons.promises.PromiseHandler;

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
