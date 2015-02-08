package com.acme.server.event;

import com.acme.engine.event.Event;
import com.badlogic.ashley.core.Entity;

public interface CombatControllerEvent extends Event {

    void onEntityDamaged(Entity attacker, Entity victim, int damage);

    void onEntityKilled(Entity killer, Entity victim);
}
