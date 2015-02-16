package com.acme.engine.application;

import java.util.concurrent.TimeUnit;

// TODO remove this later and from EntityEngine
public class NullContext implements Context {

    @Override
    public <T> void register(Class<T> clazz, T object) {
    }

    @Override
    public <T> void register(Class<T> clazz, String name, T object) {
    }

    @Override
    public <T> T get(Class<T> clazz) {
        return null;
    }

    @Override
    public <T> T get(Class<T> clazz, String name) {
        return null;
    }

    @Override
    public float getDelta() {
        return 0;
    }

    @Override
    public void schedule(Runnable task) {
    }

    @Override
    public void schedule(Runnable task, long delay, TimeUnit unit) {
    }

    @Override
    public void schedulePeriodic(Runnable task, long delay, long period, TimeUnit unit) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public void waitForStart(long timeoutMillis) {
    }

    @Override
    public void waitForDispose(long timeoutMillis) {
    }
}
