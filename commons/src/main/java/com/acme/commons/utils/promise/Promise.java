package com.acme.commons.utils.promise;

public interface Promise<D, F> {

    /**
     * Adds the given handler to resolve listeners
     *
     * @param resolveHandler handler
     * @return this promise for chaining
     */
    Promise<D, F> done(PromiseHandler<D> resolveHandler);

    /**
     * Adds the given handler to reject listeners
     *
     * @param rejectHandler handler
     * @return this promise for chaining
     */
    Promise<D, F> fail(PromiseHandler<F> rejectHandler);

    /**
     * Adds the given handlers to resolve and reject listeners
     *
     * @param resolveHandler handler
     * @param rejectHandler  handler
     * @return this promise for chaining
     */
    Promise<D, F> then(PromiseHandler<D> resolveHandler, PromiseHandler<F> rejectHandler);

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

    D getResolveResult();

    F getRejectionReason();
}
