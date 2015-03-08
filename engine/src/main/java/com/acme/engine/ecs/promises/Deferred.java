package com.acme.engine.ecs.promises;

import java.util.ArrayList;

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

    public void checkFulfilled() {
        if (!isPending()) {
            throw new IllegalStateException("Deferred object has been fulfilled");
        }
    }

    protected void notifyResolved() {
        for (PromiseHandler<D> handler : new ArrayList<>(resolveListeners)) {
            handler.handle(resolveResult);
        }
        resolveListeners.clear();
    }

    protected void notifyRejected() {
        for (PromiseHandler<F> handler : rejectListeners) {
            handler.handle(rejectReason);
        }
        rejectListeners.clear();
    }
}
