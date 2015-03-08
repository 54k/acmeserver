package com.acme.engine.mechanics.promises;

public interface PromiseHandler<T> {

    void handle(T result);
}
