package com.acme.commons.utils.promise;

import java.util.Collection;

final class DeferredGroup extends Deferred<Void, Void> {

    private final int total;
    private int resolved;
    private int rejected;

    @SuppressWarnings("unchecked")
    DeferredGroup(Collection<Promise<?, ?>> promises, GroupHandler groupHandler) {
        total = promises.size();
        PromiseHandler resolveHandler = new PromiseHandler() {
            @Override
            public void handle(Object result) {
                groupHandler.handle(DeferredGroup.this, ++resolved, rejected, total);
            }
        };

        PromiseHandler rejectHandler = new PromiseHandler() {
            @Override
            public void handle(Object result) {
                groupHandler.handle(DeferredGroup.this, resolved, ++rejected, total);
            }
        };

        for (Promise<?, ?> promise : promises) {
            promise.then(resolveHandler, rejectHandler);
        }
    }

    interface GroupHandler {
        void handle(DeferredGroup group, int resolved, int rejected, int total);
    }
}
