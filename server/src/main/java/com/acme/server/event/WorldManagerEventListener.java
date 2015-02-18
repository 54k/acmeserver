package com.acme.server.event;

import com.acme.engine.events.EventListener;
import com.badlogic.ashley.core.Entity;

public interface WorldManagerEventListener extends EventListener {

    void onEntitySpawned(Entity entity);

    void onEntityDecayed(Entity entity);
}
