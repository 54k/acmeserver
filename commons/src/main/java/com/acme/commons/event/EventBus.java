package com.acme.commons.event;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class EventBus {

    private final Map<Class<?>, Dispatcher> dispatcherByType = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends Event> T post(Class<T> type) {
        return (T) getDispatcherFor(type).proxy;
    }

    @SuppressWarnings("unchecked")
    public void register(Object listener) {
        Class aClass = listener.getClass();
        for (Class iClass : aClass.getInterfaces()) {
            if (Event.class.isAssignableFrom(iClass)) {
                register((Class<Event>) iClass, (Event) listener);
            }
        }
    }

    public <T extends Event> void register(Class<T> type, T listener) {
        validateType(type);
        getDispatcherFor(type).listeners.add(listener);
    }

    private static void validateType(Class<? extends Event> type) {
        for (Method method : type.getDeclaredMethods()) {
            if (method.getReturnType() != void.class) {
                throw new IllegalArgumentException();
            }
        }
    }

    private Dispatcher getDispatcherFor(Class<?> type) {
        dispatcherByType.putIfAbsent(type, new Dispatcher(type));
        return dispatcherByType.get(type);
    }

    @SuppressWarnings("unchecked")
    public void unregister(Object listener) {
        Class aClass = listener.getClass();
        for (Class iClass : aClass.getInterfaces()) {
            if (Event.class.isAssignableFrom(iClass)) {
                unregister((Class<Event>) iClass, (Event) listener);
            }
        }
    }

    public <T extends Event> void unregister(Class<T> type, T listener) {
        getDispatcherFor(type).listeners.remove(listener);
    }

    private static final class Dispatcher implements InvocationHandler {

        final Object proxy;
        final Set<? super Object> listeners = Collections.newSetFromMap(new ConcurrentHashMap<>());

        Dispatcher(Class<?> type) {
            proxy = Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            for (Object l : listeners) {
                method.invoke(l, args);
            }
            return null;
        }
    }
}
