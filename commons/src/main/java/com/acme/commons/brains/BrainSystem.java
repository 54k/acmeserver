package com.acme.commons.brains;

import com.acme.ecs.core.ComponentMapper;
import com.acme.ecs.core.Entity;
import com.acme.ecs.core.Aspect;
import com.acme.ecs.core.Wire;
import com.acme.ecs.systems.AspectIteratingSystem;

@Wire
public class BrainSystem extends AspectIteratingSystem {

    private static final Aspect BRAIN_ASPECT = Aspect.all(Brain.class).get();

    private ComponentMapper<Brain> brainCm;
    private Aspect aspect;

    public BrainSystem() {
        this(Aspect.ALL, 0);
    }

    public BrainSystem(Aspect aspect) {
        this(aspect, 0);
    }

    public BrainSystem(Aspect aspect, int priority) {
        super(BRAIN_ASPECT, priority);
        this.aspect = aspect;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (aspect.matches(entity) && shouldUpdateBrain(entity, deltaTime)) {
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
