package com.acme.server.impacts;

import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.Wire;
import com.acme.engine.mechanics.impact.ImpactSystem;
import com.acme.server.combat.StatsSystem;

@Wire
public class RegenImpactSystem extends ImpactSystem<RegenImpact> {

    private StatsSystem statsSystem;

    public RegenImpactSystem() {
        super(RegenImpact.class);
    }

    @Override
    protected void impactTicked(RegenImpact impact, Entity target) {
        if (!statsSystem.isDead(target)) {
            int maxHitPoints = statsSystem.getMaxHitPoints(target);
            statsSystem.addHitPoints(target, (int) (maxHitPoints * 0.02), true);
        }
    }
}
