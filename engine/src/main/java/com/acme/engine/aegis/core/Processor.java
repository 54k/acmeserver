package com.acme.engine.aegis.core;

import com.acme.engine.aegis.utils.ImmutableList;

public interface Processor {

    void processSystems(ImmutableList<EntitySystem> systems, Engine engine);
}
