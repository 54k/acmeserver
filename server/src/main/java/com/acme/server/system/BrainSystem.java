package com.acme.server.system;

import com.acme.commons.ashley.EngineListener;
import com.acme.commons.ashley.EntityEngine;
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
public class BrainSystem extends IteratingSystem implements EngineListener {

    private static final Family BRAIN_OWNERS = Family.all(PositionComponent.class, BrainComponent.class).get();

    private ComponentMapper<PositionComponent> pcm;
    private ComponentMapper<BrainComponent> bcm;

    private WorldManager worldManager;

    public BrainSystem() {
        super(BRAIN_OWNERS);
    }

    @Override
    public void addedToEngine(EntityEngine engine) {
        engine.addSystem(new PatrolState());
        engine.addSystem(new CombatState());
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
