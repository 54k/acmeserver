package com.acme.engine.ashley;

public interface EntityEngineListener {

    void addedToEngine(EntityEngine engine);

    void removedFromEngine(EntityEngine engine);
}
