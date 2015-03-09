package com.acme.commons.application;

public interface CancellableTask {

    void cancel();

    boolean isCancelled();
}
