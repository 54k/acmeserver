package com.acme.core.ashley;

public interface EntityEngineListener {

    void addedToEngine(EntityEngine engine);

    void removedFromEngine(EntityEngine engine);
}
