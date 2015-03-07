package com.acme.engine.ecs.schedule;

public abstract class Promise<T> {

    Promise() {
    }

    /**
     * Returns a {@link Promise} object that is resolved with the given value.
     * If the value is a thenable (i.e. has a then method), the returned promise will "follow" that thenable,
     * adopting its eventual state, otherwise the returned promise will be fulfilled with the value.
     *
     * @param result result
     * @return resolved promise
     */
    public Promise<T> resolve(T result) {
        return this;
    }

    /**
     * Returns a {@link Promise} object that is rejected with the given reason.
     *
     * @param reason rejection reason
     * @return rejected promise
     */
    public Promise<T> reject(Throwable reason) {
        return this;
    }
}
