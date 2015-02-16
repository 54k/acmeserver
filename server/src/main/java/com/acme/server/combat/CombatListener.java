package com.acme.server.combat;

import com.acme.engine.event.Event;
import com.badlogic.ashley.core.Entity;

public interface CombatListener extends Event {

    void onEntityDamaged(Entity attacker, Entity victim, int damage);

    void onEntityKilled(Entity killer, Entity victim);
}
