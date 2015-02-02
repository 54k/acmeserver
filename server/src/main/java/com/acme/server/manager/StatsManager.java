package com.acme.server.manager;

import com.acme.commons.ashley.ManagerSystem;
import com.acme.commons.ashley.Wired;
import com.acme.server.component.InvulnerableComponent;
import com.acme.server.component.StatsComponent;
import com.acme.server.packet.outbound.HealthPacket;
import com.acme.server.system.GameServerNetworkSystem;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

@Wired
public class StatsManager extends ManagerSystem {

    private ComponentMapper<StatsComponent> scm;
    private ComponentMapper<InvulnerableComponent> icm;

    private GameServerNetworkSystem networkSystem;

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
            networkSystem.sendPacket(entity, new HealthPacket(statsComponent.getHitPoints(), regen));
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
}
