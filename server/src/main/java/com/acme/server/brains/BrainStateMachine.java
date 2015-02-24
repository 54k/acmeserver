package com.acme.server.brains;

import com.acme.engine.ecs.core.Entity;
import com.acme.engine.mechanics.brains.Brain;
import com.acme.engine.mechanics.brains.BrainState;

import java.util.HashMap;
import java.util.Map;

public class BrainStateMachine extends Brain<Entity> {

    private final Map<Class<? extends BrainState>, BrainState<Entity>> statesByClass;

    public BrainStateMachine(Entity owner) {
        super(owner);
        statesByClass = new HashMap<>();
    }

    public void addState(BrainState<Entity> state) {
        statesByClass.put(state.getClass(), state);
    }

    public void changeState(Class<? extends BrainState<Entity>> stateClass) {
        changeState(getState(stateClass));
    }

    public boolean isInState(Class<? extends BrainState<Entity>> stateClass) {
        return isInState(getState(stateClass));
    }

    private BrainState<Entity> getState(Class<? extends BrainState<Entity>> stateClass) {
        BrainState<Entity> brainState = statesByClass.get(stateClass);
        if (brainState == null) {
            throw new NullPointerException("State for name " + stateClass.getSimpleName() + " does not exists");
        }
        return brainState;
    }
}
