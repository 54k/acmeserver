package com.acme.engine.mechanics.promises;

import java.util.Arrays;

public final class Promises {
    private Promises() {
    }

    public static Promise<Void, Void> all(Promise<?, ?>... promises) {
        return new DeferredGroup(Arrays.asList(promises));
    }

    public static <D> Promise<D, Void> resolved(D result) {
        return new Deferred<D, Void>().resolve(result);
    }

    public static <F> Promise<Void, F> rejected(F reason) {
        return new Deferred<Void, F>().reject(reason);
    }
}
