package com.acme.engine.aegis;

import com.acme.engine.utils.ImmutableList;

public interface Processor {

    void processSystems(ImmutableList<EntitySystem> systems, Engine engine);
}
