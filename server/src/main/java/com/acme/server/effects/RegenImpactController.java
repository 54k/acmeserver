package com.acme.server.effects;

import com.acme.engine.ashley.Wired;
import com.acme.engine.effects.ImpactController;
import com.acme.server.controller.StatsController;
import com.badlogic.ashley.core.Entity;

@Wired
public class RegenImpactController extends ImpactController {

    private StatsController statsController;

    public RegenImpactController() {
        super(RegenImpact.class);
    }

    @Override
    public void ticked(Entity effect, Entity target) {
        if (!statsController.isDead(target)) {
            int maxHitPoints = statsController.getMaxHitPoints(target);
            statsController.addHitPoints(target, (int) (maxHitPoints * 0.02), true);
        }
    }
}
