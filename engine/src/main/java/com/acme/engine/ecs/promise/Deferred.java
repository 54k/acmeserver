package com.acme.engine.ecs.promise;

import java.util.ArrayList;
import java.util.List;

public class Deferred<D, R> implements Promise<D, R> {

    protected State state = State.PENDING;
    protected final List<PromiseHandler<D>> resolveListeners = new ArrayList<>();
    protected final List<PromiseHandler<R>> rejectListeners = new ArrayList<>();

    protected D resolveResult;
    protected R rejectReason;

    /**
     * Returns a {@link Promise} object that is resolved with the given value.
     * If the value is a thenable (i.e. has a then method), the returned promise will "follow" that thenable,
     * adopting its eventual state, otherwise the returned promise will be fulfilled with the value.
     *
     * @param result result
     * @return resolved promise
     */
    public Promise<D, R> resolve(D result) {
        if (!isPending()) {
            throw new IllegalStateException();
        }
        state = State.RESOLVED;
        resolveResult = result;
        notifyResolved();
        return this;
    }

    /**
     * Returns a {@link Promise} object that is rejected with the given reason.
     *
     * @param reason rejection reason
     * @return rejected promise
     */
    public Promise<D, R> reject(R reason) {
        if (!isPending()) {
            throw new IllegalStateException();
        }
        state = State.REJECTED;
        rejectReason = reason;
        notifyRejected();
        return this;
    }

    protected void notifyResolved() {
        for (PromiseHandler<D> handler : new ArrayList<>(resolveListeners)) {
            handler.handle(resolveResult);
        }
        resolveListeners.clear();
    }

    protected void notifyRejected() {
        for (PromiseHandler<R> handler : rejectListeners) {
            handler.handle(rejectReason);
        }
        rejectListeners.clear();
    }

    @Override
    public Promise<D, R> done(PromiseHandler<D> resolveHandler) {
        if (isResolved()) {
            notifyResolve(resolveHandler);
        } else {
            resolveListeners.add(resolveHandler);
        }
        return this;
    }

    private void notifyResolve(PromiseHandler<D> resolveHandler) {
        resolveHandler.handle(resolveResult);
    }

    @Override
    public Promise<D, R> fail(PromiseHandler<R> rejectHandler) {
        if (isRejected()) {
            notifyReject(rejectHandler);
        } else {
            rejectListeners.add(rejectHandler);
        }
        return this;
    }

    protected void notifyReject(PromiseHandler<R> rejectHandler) {
        rejectHandler.handle(rejectReason);
    }

    @Override
    public boolean isPending() {
        return state == State.PENDING;
    }

    @Override
    public boolean isResolved() {
        return state == State.RESOLVED;
    }

    @Override
    public boolean isRejected() {
        return state == State.REJECTED;
    }

    enum State {
        PENDING, RESOLVED, REJECTED
    }
}
