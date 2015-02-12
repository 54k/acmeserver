package com.acme.engine.brain;

public class Brain<E> {

    private final E owner;

    private BrainState<E> globalState;
    private BrainState<E> currentState;

    public Brain(E owner, BrainState<E> initialState) {
        this(owner, initialState, null);
    }

    public Brain(E owner, BrainState<E> initialState, BrainState<E> globalState) {
        this.owner = owner;
        setGlobalState(globalState);
        changeState(initialState);
    }

    public void setGlobalState(BrainState<E> globalState) {
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
}
