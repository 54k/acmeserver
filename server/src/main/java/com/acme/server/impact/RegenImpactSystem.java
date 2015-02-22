package com.acme.server.impact;

import com.acme.engine.aegis.core.Entity;
import com.acme.engine.aegis.core.Wired;
import com.acme.engine.impact.ImpactSystem;
import com.acme.server.combat.StatsController;

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
