package com.acme.engine.brain;

import com.acme.engine.aegis.core.ComponentMapper;
import com.acme.engine.aegis.core.Engine;
import com.acme.engine.aegis.core.Entity;
import com.acme.engine.aegis.core.Family;
import com.acme.engine.aegis.systems.IteratingSystem;

public class BrainSystem extends IteratingSystem {

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
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        for (BrainStateController brainStateController : brainStateControllers) {
            engine.addSystem(brainStateController);
        }
    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.addedToEngine(engine);
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
