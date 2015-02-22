package com.acme.engine.ecs.utils;

import java.util.Iterator;
import java.util.List;

public final class ImmutableList<T> implements Iterable<T> {

    private List<T> wrapped;

    public ImmutableList(List<T> wrapped) {
        this.wrapped = wrapped;
    }

    public int size() {
        return wrapped.size();
    }

    public boolean isEmpty() {
        return wrapped.isEmpty();
    }

    public boolean contains(T o) {
        return wrapped.contains(o);
    }

    public Object[] toArray() {
        return wrapped.toArray();
    }

    public T[] toArray(T[] a) {
        return wrapped.toArray(a);
    }

    @Override
    public Iterator<T> iterator() {
        return wrapped.iterator();
    }

    public T get(int index) {
        return wrapped.get(index);
    }

    public int indexOf(Object o) {
        return wrapped.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return wrapped.lastIndexOf(o);
    }

    @Override
    public int hashCode() {
        return wrapped.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return wrapped.equals(o);
    }
}
