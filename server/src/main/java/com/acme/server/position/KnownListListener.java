package com.acme.server.position;

import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.events.EventListener;

public interface KnownListListener extends EventListener {

    void entityAdded(KnownListNode to, Entity entity);

    void entityRemoved(KnownListNode from, Entity entity);
}
