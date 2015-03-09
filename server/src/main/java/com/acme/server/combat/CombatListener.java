package com.acme.server.combat;

import com.acme.ecs.core.Entity;
import com.acme.ecs.events.EventListener;

public interface CombatListener extends EventListener {

    void onEntityDamaged(Entity attacker, Entity victim, int damage);

    void onEntityKilled(Entity killer, Entity victim);
}
