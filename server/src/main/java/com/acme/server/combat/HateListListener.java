package com.acme.server.combat;

import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.events.EventListener;

public interface HateListListener extends EventListener {

    void onHaterAdded(Entity entity, Entity hater);

    void onHaterRemoved(Entity entity, Entity hater);

    void onHatersEmpty(Entity entity);
}
