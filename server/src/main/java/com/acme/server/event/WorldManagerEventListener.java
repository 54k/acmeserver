package com.acme.server.event;

import com.acme.engine.aegis.core.Entity;
import com.acme.engine.aegis.events.EventListener;

public interface WorldManagerEventListener extends EventListener {

    void onEntitySpawned(Entity entity);

    void onEntityDecayed(Entity entity);
}
