package com.acme.commons.utils.promises;

public class Deferred<D, F> extends AbstractPromise<D, F> {

    /**
     * Resolves this deferred with the given result
     *
     * @param result result
     * @return this deferred for chaining
     */
    public Deferred<D, F> resolve(D result) {
        checkFulfilled();
        status = PromiseStatus.RESOLVED;
        resolveResult = result;
        notifyResolved();
        return this;
    }

    /**
     * Rejects this deferred with the given reason
     *
     * @param reason reason
     * @return this deferred for chaining
     */
    public Deferred<D, F> reject(F reason) {
        checkFulfilled();
        status = PromiseStatus.REJECTED;
        rejectReason = reason;
        notifyRejected();
        return this;
    }

    /**
     * Checks if this promise has resolved or rejected status
     *
     * @throws IllegalStateException if this promise had fulfilled
     */
    protected void checkFulfilled() {
        if (!isPending()) {
            throw new IllegalStateException("Deferred object has been fulfilled");
        }
    }
}
