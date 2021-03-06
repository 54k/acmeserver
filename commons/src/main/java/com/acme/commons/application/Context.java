package com.acme.commons.application;

import java.util.concurrent.TimeUnit;

public interface Context {

    float getDelta();

    CancellableTask schedule(Runnable task);

    CancellableTask schedule(Runnable task, long delay, TimeUnit unit);

    CancellableTask schedulePeriodic(Runnable task, long delay, long period, TimeUnit unit);

    void start();

    void waitForStart(long timeoutMillis);

    void dispose();

    void waitForDispose(long timeoutMillis);
}
