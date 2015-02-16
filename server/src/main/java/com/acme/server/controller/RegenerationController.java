package com.acme.server.controller;

import com.acme.engine.application.Context;
import com.acme.engine.ashley.Wired;
import com.acme.engine.ashley.system.EffectController;
import com.acme.server.component.RegenerationComponent;
import com.acme.server.controller.StatsController;
import com.acme.server.event.CombatControllerEvent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

@Wired
public class RegenerationController extends EffectController implements CombatControllerEvent {

    private ComponentMapper<RegenerationComponent> rcm;

    private Context context;

    private StatsController statsController;

    @Override
    public void applyEffect(Entity entity) {
        if (rcm.has(entity)) {
            return;
        }

        context.schedule(() -> {
            entity.add(new RegenerationComponent());
            getEffectList(entity).apply(this);
        });
    }

    @Override
    public void update(Entity entity, float deltaTime) {
        if (statsController.isDead(entity)) {
            return;
        }
        RegenerationComponent regenerationComponent = rcm.get(entity);
        regenerationComponent.decreaseTime(deltaTime);
        if (regenerationComponent.isReady()) {
            int maxHitPoints = statsController.getMaxHitPoints(entity);
            statsController.addHitPoints(entity, (int) (maxHitPoints * 0.02), true);
            regenerationComponent.refreshTimer();
        }
    }

    @Override
    public void onEntityDamaged(Entity attacker, Entity victim, int damage) {
        if (rcm.has(victim)) {
            rcm.get(victim).refreshTimer();
        }
    }

    @Override
    public void onEntityKilled(Entity killer, Entity victim) {
        if (rcm.has(victim)) {
            rcm.get(victim).setTime(0);
        }
    }
}
