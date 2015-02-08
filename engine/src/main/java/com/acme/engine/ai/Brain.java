package com.acme.engine.ai;

import com.badlogic.ashley.core.Entity;

public class Brain {

    private final Entity owner;

    private BrainState globalState;
    private BrainState currentState;

    public Brain(Entity owner, BrainState initialState) {
        this(owner, initialState, null);
    }

    public Brain(Entity owner, BrainState initialState, BrainState globalState) {
        this.owner = owner;
        setGlobalState(globalState);
        changeState(initialState);
    }

    public void setGlobalState(BrainState globalState) {
        this.globalState = globalState;
    }

    public void update(float deltaTime) {
        if (globalState != null) {
            globalState.update(owner, deltaTime);
        }
        if (currentState != null) {
            currentState.update(owner, deltaTime);
        }
    }

    public void changeState(BrainState state) {
        BrainState prevState = currentState;
        currentState = state;
        if (prevState != null) {
            prevState.exit(owner);
        }
        if (currentState != null) {
            state.enter(owner);
        }
    }

    public boolean isInState(BrainState state) {
        return currentState == state;
    }
}
