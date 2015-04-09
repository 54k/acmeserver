package com.acme.ecs.utils;

import java.util.LinkedList;
import java.util.Queue;

public abstract class Pool<T> {
    /**
     * The maximum number of objects that will be pooled.
     */
    public final int max;
    /**
     * The highest number of free objects. Can be dispose any time.
     */
    public int peak;

    private final Queue<T> freeObjects;

    /**
     * Creates a pool with an initial capacity of 16 and no maximum.
     */
    public Pool() {
        this(Integer.MAX_VALUE);
    }

    /**
     * @param max The maximum number of free objects to store in this pool.
     */
    public Pool(int max) {
        freeObjects = new LinkedList<>();
        this.max = max;
    }

    abstract protected T newObject();

    /**
     * Returns an object from this pool. The object may be new (from {@link #newObject()}) or reused (previously
     * {@link #free(Object) freed}).
     */
    public T obtain() {
        return freeObjects.isEmpty() ? newObject() : freeObjects.poll();
    }

    /**
     * Puts the specified object in the pool, making it eligible to be returned by {@link #obtain()}. If the pool already contains
     * {@link #max} free objects, the specified object is dispose but not added to the pool.
     */
    public void free(T object) {
        if (object == null) {
            throw new NullPointerException("object cannot be null.");
        }
        if (freeObjects.size() < max) {
            freeObjects.add(object);
            peak = Math.max(peak, freeObjects.size());
        }
        if (object instanceof Disposable) {
            ((Disposable) object).dispose();
        }
    }

    /**
     * Puts the specified objects in the pool. Null objects within the array are silently ignored.
     *
     * @see #free(Object)
     */
    public void freeAll(Iterable<T> objects) {
        if (objects == null) {
            throw new NullPointerException("object cannot be null.");
        }
        Queue<T> freeObjects = this.freeObjects;
        int max = this.max;
        for (T object : objects) {
            if (freeObjects.size() < max) {
                freeObjects.add(object);
            }
            if (object instanceof Disposable) {
                ((Disposable) object).dispose();
            }
        }
        peak = Math.max(peak, freeObjects.size());
    }

    /**
     * Removes all free objects from this pool.
     */
    public void clear() {
        freeObjects.clear();
    }

    /**
     * The number of objects available to be obtained.
     */
    public int getFree() {
        return freeObjects.size();
    }

    /**
     * Objects implementing this interface will have {@link #dispose()} called when passed to {@link #free(Object)}.
     */
    public static interface Disposable {
        /**
         * Resets the object for reuse. Object references should be nulled and fields may be set to default values.
         */
        public void dispose();
    }
}
