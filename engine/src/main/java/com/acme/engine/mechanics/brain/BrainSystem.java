package com.acme.engine.mechanics.brain;

import com.acme.engine.ecs.core.ComponentMapper;
import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.Family;
import com.acme.engine.ecs.core.Wire;
import com.acme.engine.ecs.systems.IteratingSystem;

@Wire
public class BrainSystem extends IteratingSystem {

    private static final Family BRAIN_OWNERS_FAMILY = Family.all(BrainComponent.class).get();

    private ComponentMapper<BrainComponent> brainCm;

    private Family family;

    public BrainSystem() {
        this(Family.all().get(), 0);
    }

    public BrainSystem(Family family) {
        this(family, 0);
    }

    public BrainSystem(Family family, int priority) {
        super(BRAIN_OWNERS_FAMILY, priority);
        this.family = family;
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
        Brain brain = brainCm.get(entity).getBrain();
        brain.update(deltaTime);
    }
}
