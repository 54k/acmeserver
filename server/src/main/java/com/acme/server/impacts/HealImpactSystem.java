package com.acme.server.impacts;

import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.Wire;
import com.acme.engine.mechanics.impact.ImpactSystem;
import com.acme.server.combat.StatsSystem;

@Wire
public class HealImpactSystem extends ImpactSystem<HealImpact> {

    private StatsSystem statsSystem;

    public HealImpactSystem() {
        super(HealImpact.class);
    }

    @Override
    protected void impactTicked(HealImpact impact, Entity target) {
        addHitPoints(impact, target);
    }

    private void addHitPoints(HealImpact impact, Entity target) {
        statsSystem.addHitPoints(target, impact.getAmount());
    }
}
