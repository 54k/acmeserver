package com.acme.engine.mechanics.brains;

import com.acme.engine.ecs.core.Entity;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Brain<E> {

    private final E owner;
    private final Map<Class<? extends BrainState>, BrainState<E>> statesByClass;
    private final Deque<BrainState<E>> statesStack;

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

    public Brain<E> addState(BrainState<E> state) {
        statesByClass.put(state.getClass(), state);
        return this;
    }

    public Brain<E> pushState(Class<? extends BrainState<E>> stateClass) {
        BrainState<E> state = getState(stateClass);
        if (state == currentState) {
            return this;
        }

        changeState(state);
        statesStack.addFirst(currentState);
        return this;
    }

    public Brain<E> popState() {
        if (statesStack.isEmpty()) {
            throw new IllegalStateException("States stack is empty");
        }
        BrainState<E> previousState = statesStack.pollFirst();
        changeState(previousState);
        return this;
    }

    public boolean isInState(Class<? extends BrainState<Entity>> stateClass) {
        return currentState.getClass().equals(stateClass);
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
