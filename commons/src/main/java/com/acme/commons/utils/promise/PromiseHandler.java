package com.acme.commons.utils.promise;

public interface PromiseHandler<T> {

    void handle(T result);
}
