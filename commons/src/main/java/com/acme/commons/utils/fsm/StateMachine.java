package com.acme.commons.utils.fsm;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public final class StateMachine<T> {

	private final T context;
	private final Map<Class<? extends State>, State<T>> statesByClass;
	private final Deque<State<T>> statesStack;

	private State<T> globalState;
	private State<T> currentState;

	public StateMachine(T context) {
		this(context, null);
	}

	public StateMachine(T context, State<T> initialState) {
		this(context, initialState, null);
	}

	public StateMachine(T context, State<T> initialState, State<T> globalState) {
		this.context = context;
		setGlobalState(globalState);
		changeState(initialState);

		statesByClass = new HashMap<>();
		statesStack = new LinkedList<>();
	}

	/**
	 * @return this state machine's context
	 */
	public T getContext() {
		return context;
	}

	/**
	 * Adds the given state to this state machine
	 *
	 * @param state state
	 * @return this state machine for chaining
	 */
	public StateMachine<T> addState(State<T> state) {
		statesByClass.put(state.getClass(), state);
		return this;
	}

	/**
	 * Pushes the given state to this states stack
	 *
	 * @param stateClass state type stored in this state machine
	 * @return this state machine for chaining
	 */
	public StateMachine<T> pushState(Class<? extends State<T>> stateClass) {
		State<T> state = getState(stateClass);
		return pushState(state);
	}

	/**
	 * Pushes the given state to this states stack
	 *
	 * @param state state
	 * @return this state machine for chaining
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

	// TODO null checks
	public void pushMessage(Object message) {
		if (!currentState.handleMessage(message)) {
			globalState.handleMessage(message);
		}
	}
}
