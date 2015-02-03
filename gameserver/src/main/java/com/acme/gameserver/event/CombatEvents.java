package com.acme.gameserver.event;

import com.acme.core.event.Event;
import com.badlogic.ashley.core.Entity;

public interface CombatEvents extends Event {

    void onEntityDamaged(Entity attacker, Entity victim, int damage);

    void onEntityKilled(Entity killer, Entity victim);
}
