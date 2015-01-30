package com.acme.commons.ashley;

public interface EngineListener {

    void addedToEngine(WiringEngine engine);

    void removedFromEngine(WiringEngine engine);

    void initialize();
}
