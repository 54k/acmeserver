package com.acme.server.combat;

import com.acme.engine.event.Event;
import com.badlogic.ashley.core.Entity;

public interface HateListListener extends Event {

    void onHaterAdded(Entity entity, Entity hater);

    void onHaterRemoved(Entity entity, Entity hater);

    void onHatersEmpty(Entity entity);
}
