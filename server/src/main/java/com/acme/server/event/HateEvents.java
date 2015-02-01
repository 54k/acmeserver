package com.acme.server.event;

import com.acme.commons.event.Event;
import com.badlogic.ashley.core.Entity;

public interface HateEvents extends Event {

    default void onHaterAdded(Entity entity, Entity hater) {
    }

    default void onHaterRemoved(Entity entity, Entity hater) {
    }

    default void onHatersEmpty(Entity entity) {
    }
}
