package com.acme.engine.ecs.promise;

public interface PromiseHandler<T> {

    void handle(T result);
}
