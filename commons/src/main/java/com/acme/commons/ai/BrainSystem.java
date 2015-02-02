package com.acme.commons.ai;

import com.acme.commons.ashley.EntityEngine;
import com.acme.commons.ashley.EntityEngineListener;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public abstract class BrainSystem extends IteratingSystem implements EntityEngineListener {

    private static final Family BRAIN_OWNERS_FAMILY = Family.all(BrainComponent.class).get();
    private static final ComponentMapper<BrainComponent> bcm = ComponentMapper.getFor(BrainComponent.class);

    private BrainStateController[] brainStateControllers;
    private Family family;

    public BrainSystem() {
        this(Family.all().get(), 0);
    }

    public BrainSystem(Family family, BrainStateController... brainStateControllers) {
        this(family, 0, brainStateControllers);
    }

    public BrainSystem(Family family, int priority, BrainStateController... brainStateControllers) {
        super(BRAIN_OWNERS_FAMILY, priority);
        this.family = family;
        this.brainStateControllers = brainStateControllers;
    }

    @Override
    public void addedToEngine(EntityEngine engine) {
        for (BrainStateController brainStateController : brainStateControllers) {
            engine.addSystem(brainStateController);
        }
    }

    @Override
    public void removedFromEngine(EntityEngine engine) {
        for (BrainStateController brainStateController : brainStateControllers) {
            engine.removeSystem(brainStateController);
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (family.matches(entity) && shouldUpdateBrain(entity, deltaTime)) {
            updateBrain(entity, deltaTime);
        }
    }

    protected abstract boolean shouldUpdateBrain(Entity entity, float deltaTime);

    private void updateBrain(Entity entity, float deltaTime) {
        Brain brain = bcm.get(entity).getBrain();
        brain.update(deltaTime);
    }
}