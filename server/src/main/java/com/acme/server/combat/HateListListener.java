package com.acme.server.combat;

import com.acme.engine.events.EventListener;
import com.badlogic.ashley.core.Entity;

public interface HateListListener extends EventListener {

    void onHaterAdded(Entity entity, Entity hater);

    void onHaterRemoved(Entity entity, Entity hater);

    void onHatersEmpty(Entity entity);
}
