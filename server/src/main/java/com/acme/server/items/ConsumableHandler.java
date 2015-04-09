package com.acme.server.items;

import com.acme.ecs.core.Entity;

public interface ConsumableHandler {

    void consume(Entity consumer, Entity consumable);
}
