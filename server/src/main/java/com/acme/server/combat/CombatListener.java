package com.acme.server.combat;

import com.acme.engine.events.EventListener;
import com.badlogic.ashley.core.Entity;

public interface CombatListener extends EventListener {

    void onEntityDamaged(Entity attacker, Entity victim, int damage);

    void onEntityKilled(Entity killer, Entity victim);
}
