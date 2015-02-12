package com.acme.engine.ashley.system;

import com.acme.engine.ashley.EntityEngine;
import com.acme.engine.ashley.EntityEngineListener;
import com.acme.engine.ashley.component.BrainComponent;
import com.acme.engine.brain.Brain;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class BrainSystem extends IteratingSystem implements EntityEngineListener {

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

    protected boolean shouldUpdateBrain(Entity entity, float deltaTime) {
        return true;
    }

    private void updateBrain(Entity entity, float deltaTime) {
        Brain brain = bcm.get(entity).getBrain();
        brain.update(deltaTime);
    }
}
