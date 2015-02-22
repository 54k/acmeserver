package com.acme.engine.mechanics.brains;

public class Brain<E> {

    private final E owner;

    private BrainState<E> globalState;
    private BrainState<E> currentState;

    public Brain(E owner) {
        this(owner, null);
    }

    public Brain(E owner, BrainState<E> initialState) {
        this(owner, initialState, null);
    }

    public Brain(E owner, BrainState<E> initialState, BrainState<E> globalState) {
        this.owner = owner;
        setGlobalState(globalState);
        changeState(initialState);
    }

    public void setGlobalState(BrainState<E> globalState) {
        if (this.globalState != null) {
            this.globalState.exit(owner);
        }
        this.globalState = globalState;
        if (this.globalState != null) {
            this.globalState.enter(owner);
        }
    }

    public void update(float deltaTime) {
        if (globalState != null) {
            globalState.update(owner, deltaTime);
        }
        if (currentState != null) {
            currentState.update(owner, deltaTime);
        }
    }

    public void changeState(BrainState<E> state) {
        BrainState<E> prevState = currentState;
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

    public void clear() {
        if (globalState != null) {
            globalState.exit(owner);
        }
        globalState = null;
        if (currentState != null) {
            currentState.exit(owner);
        }
        currentState = null;
    }
}
