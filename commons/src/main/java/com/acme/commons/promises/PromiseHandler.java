package com.acme.commons.promises;

public interface PromiseHandler<T> {

    void handle(T result);
}
