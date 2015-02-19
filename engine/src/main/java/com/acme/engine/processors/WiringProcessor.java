package com.acme.engine.processors;

import com.acme.engine.aegis.Engine;
import com.acme.engine.aegis.EntitySystem;
import com.acme.engine.aegis.Processor;
import com.acme.engine.utils.ImmutableList;

public class WiringProcessor implements Processor {

    @Override
    public void processSystems(ImmutableList<EntitySystem> systems, Engine engine) {
        for (EntitySystem system : systems) {
            processSystem(system, engine);
        }
    }

    private void processSystem(EntitySystem system, Engine engine) {
    }
}
