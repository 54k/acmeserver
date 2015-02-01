package com.acme.server.event;

import com.acme.commons.event.Event;
import com.badlogic.ashley.core.Entity;

public interface CombatEvents extends Event {

    default void onEntityDamaged(Entity attacker, Entity victim, int damage) {
    }

    default void onEntityKilled(Entity killer, Entity victim) {
    }
}
