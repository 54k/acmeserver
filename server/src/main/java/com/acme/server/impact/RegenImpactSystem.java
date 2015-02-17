package com.acme.server.impact;

import com.acme.engine.ashley.Wired;
import com.acme.engine.impact.ImpactSystem;
import com.acme.server.combat.StatsController;
import com.badlogic.ashley.core.Entity;

@Wired
public class RegenImpactSystem extends ImpactSystem<RegenImpact> {

    private StatsController statsController;

    public RegenImpactSystem() {
        super(RegenImpact.class);
    }

    @Override
    protected void impactTicked(RegenImpact impact, Entity target) {
        if (!statsController.isDead(target)) {
            int maxHitPoints = statsController.getMaxHitPoints(target);
            statsController.addHitPoints(target, (int) (maxHitPoints * 0.02), true);
        }
    }
}
