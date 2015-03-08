package com.acme.engine.mechanics.timer;

import com.acme.engine.mechanics.promises.Promise;
import com.acme.engine.mechanics.promises.PromiseHandler;

public interface PromiseTask<T> extends Promise<T, Throwable> {

    @Override
    PromiseTask<T> done(PromiseHandler<T> resolve);

    @Override
    PromiseTask<T> fail(PromiseHandler<Throwable> reject);

    PromiseTask<T> cancel(PromiseHandler<Void> cancelHandler);

    void cancel();

    boolean isCancelled();
}
