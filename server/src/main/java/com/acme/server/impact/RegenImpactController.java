package com.acme.server.impact;

import com.acme.engine.ashley.Wired;
import com.acme.engine.impact.ImpactController;
import com.acme.server.combat.StatsController;
import com.badlogic.ashley.core.Entity;

@Wired
public class RegenImpactController extends ImpactController<RegenImpact> {

    private StatsController statsController;

    public RegenImpactController() {
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
