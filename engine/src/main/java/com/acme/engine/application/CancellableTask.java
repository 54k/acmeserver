package com.acme.engine.application;

public interface CancellableTask {

    void cancel();

    boolean isCancelled();
}
