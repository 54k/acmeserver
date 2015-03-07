package com.acme.engine.ecs.promise;

public interface Promise<D, R> {

    Promise<D, R> done(PromiseHandler<D> resolveHandler);

    Promise<D, R> fail(PromiseHandler<R> rejectHandler);

    boolean isPending();

    boolean isResolved();

    boolean isRejected();
}
