package com.acme.server.system;

import com.acme.commons.ashley.EntityEngine;
import com.acme.commons.ashley.EntityEngineListener;
import com.acme.commons.ashley.Wired;
import com.acme.server.ai.CombatState;
import com.acme.server.ai.PatrolState;
import com.acme.server.component.BrainComponent;
import com.acme.server.component.PositionComponent;
import com.acme.server.manager.WorldManager;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

@Wired
public class BrainSystem extends IteratingSystem implements EntityEngineListener {

    private static final Family BRAIN_OWNERS_FAMILY = Family.all(PositionComponent.class, BrainComponent.class).get();

    private ComponentMapper<PositionComponent> pcm;
    private ComponentMapper<BrainComponent> bcm;

    private WorldManager worldManager;

    private PatrolState patrolState;
    private CombatState combatState;

    public BrainSystem() {
        super(BRAIN_OWNERS_FAMILY);
    }

    @Override
    public void addedToEngine(EntityEngine engine) {
        engine.addSystem(new PatrolState());
        engine.addSystem(new CombatState());
    }

    @Override
    public void removedFromEngine(EntityEngine engine) {
        engine.removeSystem(patrolState);
        engine.removeSystem(combatState);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent positionComponent = pcm.get(entity);
        boolean isSpawned = positionComponent.isSpawned();
        boolean isRegionActive = positionComponent.getRegion().isActive();
        if (isSpawned && isRegionActive) {
            updateBrain(entity, deltaTime);
        }
    }

    private void updateBrain(Entity entity, float deltaTime) {
        BrainComponent brainComponent = bcm.get(entity);
        brainComponent.getBrain().update(deltaTime);
    }
}
