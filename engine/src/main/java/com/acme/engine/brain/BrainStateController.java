package com.acme.engine.brain;

import com.acme.engine.aegis.core.ComponentMapper;
import com.acme.engine.aegis.core.Entity;
import com.acme.engine.aegis.systems.PassiveSystem;

public abstract class BrainStateController extends PassiveSystem implements BrainState<Entity> {

    private static final ComponentMapper<BrainComponent> bcm = ComponentMapper.getFor(BrainComponent.class);

    @Override
    public void enter(Entity entity) {
    }

    @Override
    public void update(Entity entity, float deltaTime) {
    }

    @Override
    public void exit(Entity entity) {
    }

    public void changeState(Entity entity, BrainState<Entity> state) {
        Brain<Entity> brain = bcm.get(entity).getBrain();
        if (!brain.isInState(state)) {
            brain.changeState(state);
        }
    }
}
