package com.acme.server.controller;

import com.acme.commons.ashley.ManagerSystem;
import com.acme.commons.ashley.Wired;
import com.acme.server.component.InvulnerableComponent;
import com.acme.server.component.StatsComponent;
import com.acme.server.event.CombatControllerEvent;
import com.acme.server.packet.outbound.HealthPacket;
import com.acme.server.system.PacketSystem;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

@Wired
public class StatsController extends ManagerSystem implements CombatControllerEvent {

    private ComponentMapper<StatsComponent> scm;
    private ComponentMapper<InvulnerableComponent> icm;

    private PacketSystem packetSystem;

    @Override
    public void onEntityDamaged(Entity attacker, Entity victim, int damage) {
    }

    @Override
    public void onEntityKilled(Entity killer, Entity victim) {
        resetHitPoints(victim);
    }

    public void addHitPoints(Entity entity, int amount) {
        addHitPoints(entity, amount, false);
    }

    public void addHitPoints(Entity entity, int amount, boolean regen) {
        amount = adjustAmount(entity, amount);
        StatsComponent statsComponent = scm.get(entity);
        int maxHitPoints = statsComponent.getMaxHitPoints();
        int hitPoints = statsComponent.getHitPoints();
        int newHitPoints = Math.max(0, Math.min(hitPoints + amount, maxHitPoints));
        statsComponent.setHitPoints(newHitPoints);
        if (newHitPoints != hitPoints) {
            packetSystem.sendPacket(entity, new HealthPacket(statsComponent.getHitPoints(), regen));
        }
    }

    private int adjustAmount(Entity entity, int amount) {
        if (icm.has(entity)) {
            amount = Math.max(0, amount);
        }
        return amount;
    }

    public void resetHitPoints(Entity entity) {
        StatsComponent statsComponent = scm.get(entity);
        statsComponent.setHitPoints(statsComponent.getMaxHitPoints());
    }

    public boolean isDead(Entity entity) {
        return scm.get(entity).getHitPoints() == 0;
    }
}
