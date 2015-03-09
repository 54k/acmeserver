package com.acme.commons.brains;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class BrainStateMachine<E> {

    private final E owner;
    private final Map<Class<? extends BrainState>, BrainState<E>> statesByClass;
    private final Deque<BrainState<E>> statesStack;

    private BrainState<E> globalState;
    private BrainState<E> currentState;

    public BrainStateMachine(E owner) {
        this(owner, null);
    }

    public BrainStateMachine(E owner, BrainState<E> initialState) {
        this(owner, initialState, null);
    }

    public BrainStateMachine(E owner, BrainState<E> initialState, BrainState<E> globalState) {
        this.owner = owner;
        setGlobalState(globalState);
        changeState(initialState);

        statesByClass = new HashMap<>();
        statesStack = new LinkedList<>();
    }

    public E getOwner() {
        return owner;
    }

    public BrainStateMachine<E> addState(BrainState<E> state) {
        statesByClass.put(state.getClass(), state);
        return this;
    }

    public BrainStateMachine<E> pushState(Class<? extends BrainState<E>> stateClass) {
        BrainState<E> state = getState(stateClass);
        return pushState(state);
    }

    public BrainStateMachine<E> pushState(BrainState<E> state) {
        if (state == currentState) {
            return this;
        }

        statesStack.addFirst(currentState);
        changeState(state);
        return this;
    }

    public BrainStateMachine<E> popState() {
        if (statesStack.isEmpty()) {
            throw new IllegalStateException("States stack is empty");
        }
        BrainState<E> previousState = statesStack.pollFirst();
        changeState(previousState);
        return this;
    }

    public boolean isInState(Class<? extends BrainState<E>> stateClass) {
        return isInState(getState(stateClass));
    }

    public boolean isInState(BrainState<E> state) {
        return currentState == state;
    }

    public BrainState<E> getCurrentState() {
        return currentState;
    }

    private BrainState<E> getState(Class<? extends BrainState<E>> stateClass) {
        BrainState<E> brainState = statesByClass.get(stateClass);
        if (brainState == null) {
            throw new NullPointerException("State for class " + stateClass.getSimpleName() + " does not exists");
        }
        return brainState;
    }

    public void setGlobalState(BrainState<E> globalState) {
        if (this.globalState != null) {
            this.globalState.exit(this);
        }
        this.globalState = globalState;
        if (this.globalState != null) {
            this.globalState.enter(this);
        }
    }

    public void update(float deltaTime) {
        if (globalState != null) {
            globalState.update(this, deltaTime);
        }
        if (currentState != null) {
            currentState.update(this, deltaTime);
        }
    }

    public void changeState(Class<? extends BrainState<E>> stateClass) {
        BrainState<E> state = getState(stateClass);
        changeState(state);
    }

    public void changeState(BrainState<E> state) {
        BrainState<E> prevState = currentState;
        currentState = state;
        if (prevState != null) {
            prevState.exit(this);
        }
        if (currentState != null) {
            state.enter(this);
        }
    }

    public void clear() {
        if (globalState != null) {
            globalState.exit(this);
        }
        globalState = null;
        if (currentState != null) {
            currentState.exit(this);
        }
        currentState = null;
        statesStack.clear();
        statesByClass.clear();
    }
}
