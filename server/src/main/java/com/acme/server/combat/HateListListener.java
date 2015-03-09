package com.acme.server.combat;

import com.acme.ecs.core.Entity;
import com.acme.ecs.events.EventListener;

public interface HateListListener extends EventListener {

    void onHaterAdded(Entity entity, Entity hater);

    void onHaterRemoved(Entity entity, Entity hater);

    void onHatersEmpty(Entity entity);
}
