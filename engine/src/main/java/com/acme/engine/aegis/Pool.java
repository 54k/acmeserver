package com.acme.engine.aegis;

public abstract class Pool<T> {

    public T obtain() {
        return newObject();
    }

    public void free(T object) {
    }

    protected abstract T newObject();

    public static interface Poolable {
        void reset();
    }
}
