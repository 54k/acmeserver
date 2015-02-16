package com.acme.server.impact;

import com.acme.engine.ashley.Wired;
import com.acme.engine.impact.ImpactController;
import com.acme.server.combat.StatsController;
import com.badlogic.ashley.core.Entity;

@Wired
public class HealImpactController extends ImpactController<HealImpact> {

    private StatsController statsController;

    public HealImpactController() {
        super(HealImpact.class);
    }

    @Override
    protected void impactTicked(HealImpact impact, Entity target) {
        addHitPoints(impact, target);
    }

    private void addHitPoints(HealImpact impact, Entity target) {
        statsController.addHitPoints(target, impact.getAmount());
    }
}