package com.acme.engine.application;

import java.util.concurrent.TimeUnit;

public interface Context {

    <T> void register(Class<T> clazz, T object);

    <T> void register(Class<T> clazz, String name, T object);

    <T> T get(Class<T> clazz);

    <T> T get(Class<T> clazz, String name);

    float getDelta();

    void schedule(Runnable task);

    void schedule(Runnable task, long delay, TimeUnit unit);

    void schedulePeriodic(Runnable task, long delay, long period, TimeUnit unit);

    void dispose();

    void waitForStart(long timeoutMillis);

    void waitForDispose(long timeoutMillis);
}
