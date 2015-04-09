package com.acme.server.combat;

import com.acme.ecs.core.ComponentMapper;
import com.acme.ecs.core.Entity;
import com.acme.ecs.core.Aspect;
import com.acme.ecs.core.Wire;
import com.acme.ecs.systems.PassiveSystem;
import com.acme.server.impacts.InvulImpactSystem;
import com.acme.server.packets.PacketSystem;
import com.acme.server.packets.outbound.HealthPacket;

@Wire
public class StatsSystem extends PassiveSystem {

    public static final Aspect STATS_ASPECT = Aspect.all(Stats.class).get();

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
