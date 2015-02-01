package com.acme.commons.ashley;

public interface EngineListener {

    default void addedToEngine(EntityEngine engine) {
    }

    default void removedFromEngine(EntityEngine engine) {
    }

    default void initialize() {
    }
}
