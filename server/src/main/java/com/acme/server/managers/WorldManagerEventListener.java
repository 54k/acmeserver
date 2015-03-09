package com.acme.server.managers;

import com.acme.ecs.core.Entity;
import com.acme.ecs.events.EventListener;

public interface WorldManagerEventListener extends EventListener {

    void onEntitySpawned(Entity entity);

    void onEntityDecayed(Entity entity);
}
