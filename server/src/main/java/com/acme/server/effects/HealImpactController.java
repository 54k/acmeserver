package com.acme.server.effects;

import com.acme.engine.ashley.Wired;
import com.acme.engine.effects.ImpactController;
import com.acme.server.controller.StatsController;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

@Wired
public class HealImpactController extends ImpactController {

    private ComponentMapper<HealImpact> healImpactCm;

    private StatsController statsController;

    public HealImpactController() {
        super(HealImpact.class);
    }

    @Override
    public void stacked(Entity effect, Entity target) {
        addHitPoints(effect, target);
    }

    @Override
    public void ticked(Entity effect, Entity target) {
        addHitPoints(effect, target);
    }

    private void addHitPoints(Entity effect, Entity target) {
        int amount = healImpactCm.get(effect).getAmount();
        statsController.addHitPoints(target, amount);
    }
}
