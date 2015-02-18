package com.acme.engine.events;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public final class Event<T extends EventListener> {

    private Dispatcher<T> dispatcher;

    public Event(Class<T> listenerType) {
        validateType(listenerType);
        this.dispatcher = new Dispatcher<>(listenerType);
    }

    private static void validateType(Class<? extends EventListener> listenerType) {
        for (Method method : listenerType.getDeclaredMethods()) {
            if (method.getReturnType() != void.class) {
                throw new IllegalArgumentException();
            }
        }
    }

    public void add(T listener) {
        dispatcher.listeners.add(listener);
    }

    public void remove(T listener) {
        dispatcher.listeners.remove(listener);
    }

    public T dispatch() {
        return dispatcher.proxy;
    }

    @SuppressWarnings("unchecked")
    private static class Dispatcher<T extends EventListener> implements InvocationHandler {

        final T proxy;
        final List<? super Object> listeners = new ArrayList<>();

        Dispatcher(Class<T> type) {
            proxy = (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            for (Object l : new ArrayList<>(listeners)) {
                method.invoke(l, args);
            }
            return null;
        }
    }
}
