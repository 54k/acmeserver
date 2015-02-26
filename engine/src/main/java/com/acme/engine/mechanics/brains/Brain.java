package com.acme.engine.mechanics.brains;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Brain<E> {

    private final E owner;

    private Map<Class<? extends BrainState>, BrainState<E>> statesByClass;
    private Deque<BrainState<E>> statesStack;

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

        statesByClass = new HashMap<>();
        statesStack = new LinkedList<>();
    }

    public void addState(BrainState<E> state) {
        statesByClass.put(state.getClass(), state);
    }

    private BrainState<E> getState(Class<? extends BrainState<E>> stateClass) {
        BrainState<E> brainState = statesByClass.get(stateClass);
        if (brainState == null) {
            throw new NullPointerException("State for name " + stateClass.getSimpleName() + " does not exists");
        }
        return brainState;
    }

    public void pushState(Class<? extends BrainState<E>> stateClass) {
        BrainState<E> state = getState(stateClass);
        statesStack.addFirst(state);
    }

    public void popState() {
        statesStack.pollFirst();
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
