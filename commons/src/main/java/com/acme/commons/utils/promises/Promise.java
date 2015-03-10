package com.acme.commons.utils.promises;

public interface Promise<D, F> {

    Promise<D, F> done(PromiseHandler<D> resolve);

    Promise<D, F> fail(PromiseHandler<F> reject);

    Promise<D, F> then(PromiseHandler<D> resolve, PromiseHandler<F> reject);

    /**
     * Checks whether this promise is pending for resolution
     *
     * @return true if  promise is pending for resolution, false otherwise
     */
    boolean isPending();

    /**
     * Checks whether this promise is resolved
     *
     * @return true if promise is resolved, false otherwise
     */
    boolean isResolved();

    /**
     * Checks whether this promise is rejected
     *
     * @return true if promise is rejected, false otherwise
     */
    boolean isRejected();
}
