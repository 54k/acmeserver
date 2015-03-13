package com.acme.commons.utils.fsm;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public final class StateMachine<T> {

	private final T owner;
	private final Map<Class<? extends State>, State<T>> statesByClass;
	private final Deque<State<T>> statesStack;

	private State<T> globalState;
	private State<T> currentState;

	public StateMachine(T owner) {
		this(owner, null);
	}

	public StateMachine(T owner, State<T> initialState) {
		this(owner, initialState, null);
	}

	public StateMachine(T owner, State<T> initialState, State<T> globalState) {
		this.owner = owner;
		setGlobalState(globalState);
		changeState(initialState);

		statesByClass = new HashMap<>();
		statesStack = new LinkedList<>();
	}

	public T getOwner() {
		return owner;
	}

	public StateMachine<T> addState(State<T> state) {
		statesByClass.put(state.getClass(), state);
		return this;
	}

	public StateMachine<T> pushState(Class<? extends State<T>> stateClass) {
		State<T> state = getState(stateClass);
		return pushState(state);
	}

	/**
	 * Pushes state
	 *
	 * @param state
	 * @return
	 */
	public StateMachine<T> pushState(State<T> state) {
		if (state == currentState) {
			return this;
		}

		statesStack.addFirst(currentState);
		changeState(state);
		return this;
	}

	/**
	 * Pops current state from states stack
	 *
	 * @return this state machine for chaining
	 * @throws java.lang.IllegalStateException is states stack is empty
	 */
	public StateMachine<T> popState() {
		if (statesStack.isEmpty()) {
			throw new IllegalStateException("States stack is empty");
		}
		State<T> previousState = statesStack.pollFirst();
		changeState(previousState);
		return this;
	}

	public boolean isInState(Class<? extends State<T>> stateClass) {
		return isInState(getState(stateClass));
	}

	public boolean isInState(State<T> state) {
		return currentState == state;
	}

	public State<T> getCurrentState() {
		return currentState;
	}

	private State<T> getState(Class<? extends State<T>> stateClass) {
		State<T> state = statesByClass.get(stateClass);
		if (state == null) {
			throw new NullPointerException("State for class " + stateClass.getSimpleName() + " does not exists");
		}
		return state;
	}

	public void setGlobalState(State<T> globalState) {
		if (this.globalState != null) {
			this.globalState.exit(this);
		}
		this.globalState = globalState;
		if (this.globalState != null) {
			this.globalState.enter(this);
		}
	}

	/**
	 * Updates global and current states
	 *
	 * @param deltaTime time delta since last update
	 */
	public void update(float deltaTime) {
		if (globalState != null) {
			globalState.update(this, deltaTime);
		}
		if (currentState != null) {
			currentState.update(this, deltaTime);
		}
	}

	public void changeState(Class<? extends State<T>> stateClass) {
		State<T> state = getState(stateClass);
		changeState(state);
	}

	public void changeState(State<T> state) {
		State<T> prevState = currentState;
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
