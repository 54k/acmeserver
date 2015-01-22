package com.acme.server.manager;

import com.acme.commons.ashley.ManagerSystem;
import com.acme.commons.ashley.Wired;
import com.acme.server.component.InvulnerableComponent;
import com.acme.server.component.StatsComponent;
import com.acme.server.packet.outbound.HealthPacket;
import com.acme.server.system.NetworkSystem;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

@Wired
public class StatsManager extends ManagerSystem {

    private ComponentMapper<StatsComponent> scm;
    private ComponentMapper<InvulnerableComponent> icm;

    private NetworkSystem networkSystem;

    public void addHitPoints(Entity entity, int amount) {
        if (icm.has(entity)) {
            amount = Math.max(0, amount);
        }
        StatsComponent statsComponent = scm.get(entity);
        int maxHitPoints = statsComponent.getMaxHitPoints();
        int hitPoints = statsComponent.getHitPoints() + amount;
        statsComponent.setHitPoints(Math.max(0, Math.min(hitPoints, maxHitPoints)));
        networkSystem.sendPacket(entity, new HealthPacket(statsComponent.getHitPoints(), amount >= 0));
    }

    public void resetHitPoints(Entity entity) {
        StatsComponent statsComponent = scm.get(entity);
        statsComponent.setHitPoints(statsComponent.getMaxHitPoints());
    }
}
