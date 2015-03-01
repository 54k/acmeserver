package com.acme.server.impacts;

import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.Wire;
import com.acme.engine.mechanics.impact.ImpactSystem;
import com.acme.server.combat.StatsController;

@Wire
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
