package com.acme.server.event;

import com.acme.engine.event.Event;
import com.badlogic.ashley.core.Entity;

public interface WorldManagerEvent extends Event {

    void onEntitySpawned(Entity entity);

    void onEntityDecayed(Entity entity);
}
