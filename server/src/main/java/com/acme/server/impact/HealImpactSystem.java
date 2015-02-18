package com.acme.server.impact;

import com.acme.engine.processors.Wired;
import com.acme.engine.impact.ImpactSystem;
import com.acme.server.combat.StatsController;
import com.badlogic.ashley.core.Entity;

@Wired
public class HealImpactSystem extends ImpactSystem<HealImpact> {

    private StatsController statsController;

    public HealImpactSystem() {
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
