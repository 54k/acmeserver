package com.acme.commons.promises;

import java.util.ArrayList;
import java.util.List;

abstract class AbstractPromise<D, F> implements Promise<D, F> {

    protected final List<PromiseHandler<D>> resolveListeners = new ArrayList<>();
    protected final List<PromiseHandler<F>> rejectListeners = new ArrayList<>();
    protected final List<PromiseHandler> alwaysListeners = new ArrayList<>();

    protected PromiseStatus status = PromiseStatus.PENDING;
    protected D resolveResult;
    protected F rejectReason;

    @Override
    public Promise<D, F> done(PromiseHandler<D> resolve) {
        if (isResolved()) {
            notifyResolved(resolve);
        } else if (isPending()) {
            resolveListeners.add(resolve);
        }
        return this;
    }

    private void notifyResolved(PromiseHandler<D> resolve) {
        resolve.handle(resolveResult);
    }

    protected void notifyResolved() {
        for (PromiseHandler<D> handler : resolveListeners) {
            handler.handle(resolveResult);
        }
        resolveListeners.clear();
    }

    @Override
    public Promise<D, F> fail(PromiseHandler<F> reject) {
        if (isRejected()) {
            notifyRejected(reject);
        } else if (isPending()) {
            rejectListeners.add(reject);
        }
        return this;
    }

    protected void notifyRejected(PromiseHandler<F> rejectHandler) {
        rejectHandler.handle(rejectReason);
    }

    protected void notifyRejected() {
        for (PromiseHandler<F> handler : rejectListeners) {
            handler.handle(rejectReason);
        }
        rejectListeners.clear();
    }

    @Override
    public Promise<D, F> then(PromiseHandler<D> resolve, PromiseHandler<F> reject) {
        return done(resolve).fail(reject);
    }

    @Override
    public boolean isPending() {
        return status == PromiseStatus.PENDING;
    }

    @Override
    public boolean isResolved() {
        return status == PromiseStatus.RESOLVED;
    }

    @Override
    public boolean isRejected() {
        return status == PromiseStatus.REJECTED;
    }
}
