package com.acme.server.event;

import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.events.EventListener;

public interface WorldManagerEventListener extends EventListener {

    void onEntitySpawned(Entity entity);

    void onEntityDecayed(Entity entity);
}
