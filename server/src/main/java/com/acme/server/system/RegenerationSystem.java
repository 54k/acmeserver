package com.acme.server.system;

import com.acme.engine.ashley.Wired;
import com.acme.server.component.StatsComponent;
import com.acme.server.controller.StatsController;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;

@Wired
public class RegenerationSystem extends IntervalIteratingSystem {

    private static final Family STATS_OWNER_FAMILY = Family.all(StatsComponent.class).get();

    private StatsController statsController;

    public RegenerationSystem() {
        super(STATS_OWNER_FAMILY, 1000);
    }

    @Override
    protected void processEntity(Entity entity) {
        int maxHitPoints = statsController.getMaxHitPoints(entity);
        statsController.addHitPoints(entity, (int) (maxHitPoints * 0.02), true);
    }
}
