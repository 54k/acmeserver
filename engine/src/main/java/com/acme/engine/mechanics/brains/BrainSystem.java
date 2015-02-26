package com.acme.engine.mechanics.brains;

import com.acme.engine.ecs.core.ComponentMapper;
import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.Family;
import com.acme.engine.ecs.core.Wire;
import com.acme.engine.ecs.systems.IteratingSystem;

@Wire
public class BrainSystem extends IteratingSystem {

    private static final Family BRAIN_OWNERS_FAMILY = Family.all(BrainHolder.class).get();

    private ComponentMapper<BrainHolder> brainCm;

    private Family family;

    public BrainSystem() {
        this(Family.ALL, 0);
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
        BrainStateMachine brainStateMachine = getBrainStateMachine(entity);
        brainStateMachine.update(deltaTime);
    }

    protected final BrainStateMachine<Entity> getBrainStateMachine(Entity entity) {
        return brainCm.get(entity).brainStateMachine;
    }
}
