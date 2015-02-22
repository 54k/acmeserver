package com.acme.server.combat;

import com.acme.engine.aegis.core.ComponentMapper;
import com.acme.engine.aegis.core.Entity;
import com.acme.engine.aegis.core.Family;
import com.acme.engine.aegis.core.Wired;
import com.acme.engine.aegis.systems.PassiveSystem;
import com.acme.server.impact.InvulImpactSystem;
import com.acme.server.packet.outbound.HealthPacket;
import com.acme.server.system.PacketSystem;

@Wired
public class StatsController extends PassiveSystem {

    public static final Family statsFamily = Family.all(Stats.class).get();

    private ComponentMapper<Stats> statsCm;
    private InvulImpactSystem invulImpactSystem;
    private PacketSystem packetSystem;

    public int getMaxHitPoints(Entity entity) {
        return statsCm.get(entity).maxHitPoints;
    }

    public void addHitPoints(Entity entity, int amount) {
        addHitPoints(entity, amount, false);
    }

    public void addHitPoints(Entity entity, int amount, boolean regen) {
        amount = adjustAmount(entity, amount);
        Stats stats = statsCm.get(entity);
        int maxHitPoints = stats.maxHitPoints;
        int hitPoints = stats.hitPoints;
        int newHitPoints = Math.max(0, Math.min(hitPoints + amount, maxHitPoints));
        stats.hitPoints = newHitPoints;
        if (newHitPoints != hitPoints) {
            packetSystem.sendPacket(entity, new HealthPacket(stats.hitPoints, regen));
        }
    }

    private int adjustAmount(Entity entity, int amount) {
        return invulImpactSystem.hasImpact(entity) ? Math.max(0, amount) : amount;
    }

    public void resetHitPoints(Entity entity) {
        statsCm.get(entity).resetHitPoints();
    }

    public boolean isDead(Entity entity) {
        return statsCm.get(entity).hitPoints == 0;
    }

    public void setMaxHitPoints(Entity entity, int maxHitPoints) {
        statsCm.get(entity).maxHitPoints = maxHitPoints;
    }

    public void setMaxHitPointsAndReset(Entity entity, int maxHitPoints) {
        setMaxHitPoints(entity, maxHitPoints);
        resetHitPoints(entity);
    }
}
