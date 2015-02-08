package com.acme.engine.ashley.system;

import com.acme.engine.ai.Brain;
import com.acme.engine.ai.BrainState;
import com.acme.engine.ashley.component.BrainComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

public abstract class BrainStateController extends ManagerSystem implements BrainState {

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

    public void changeState(Entity entity, BrainState state) {
        Brain brain = bcm.get(entity).getBrain();
        if (!brain.isInState(state)) {
            brain.changeState(state);
        }
    }
}
