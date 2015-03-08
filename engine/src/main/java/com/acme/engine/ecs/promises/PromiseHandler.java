package com.acme.engine.ecs.promises;

public interface PromiseHandler<T> {

    void handle(T result);
}
