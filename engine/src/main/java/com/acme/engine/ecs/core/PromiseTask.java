package com.acme.engine.ecs.core;

import com.acme.engine.ecs.promise.Promise;

public interface PromiseTask<T> extends Promise<T, Throwable> {

    void cancel();

    boolean isCancelled();
}
