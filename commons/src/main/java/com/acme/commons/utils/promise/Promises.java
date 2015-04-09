package com.acme.commons.utils.promise;

import java.util.Arrays;

public final class Promises {

    private static final AllOfGroupHandler ALL_OF_HANDLER = new AllOfGroupHandler();
    private static final AnyOfGroupHandler ANY_OF_HANDLER = new AnyOfGroupHandler();

    private Promises() {
    }

    public static Promise<Void, Void> allOf(Promise<?, ?>... promises) {
        return new DeferredGroup(Arrays.asList(promises), ALL_OF_HANDLER);
    }

    public static Promise<Void, Void> anyOf(Promise<?, ?>... promises) {
        return new DeferredGroup(Arrays.asList(promises), ANY_OF_HANDLER);
    }

    public static <D> Promise<D, Void> resolved(D result) {
        return new Deferred<D, Void>().resolve(result);
    }

    public static <F> Promise<Void, F> rejected(F reason) {
        return new Deferred<Void, F>().reject(reason);
    }

    private static final class AllOfGroupHandler implements DeferredGroup.GroupHandler {

        @Override
        public void handle(DeferredGroup group, int resolved, int rejected, int total) {
            if (resolved + rejected < total) {
                return;
            }
            if (resolved == total) {
                group.resolve(null);
            } else {
                group.reject(null);
            }
        }
    }

    private static final class AnyOfGroupHandler implements DeferredGroup.GroupHandler {

        @Override
        public void handle(DeferredGroup group, int resolved, int rejected, int total) {
            if (resolved + rejected < total) {
                return;
            }

            if (group.isPending() && resolved > 0) {
                group.resolve(null);
            } else if (rejected == total) {
                group.reject(null);
            }
        }
    }
}
