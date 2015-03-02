package com.acme.server.items;

import com.acme.engine.ecs.core.Entity;

public interface ConsumableHandler {

    void consume(Entity consumer, Entity consumable);
}
