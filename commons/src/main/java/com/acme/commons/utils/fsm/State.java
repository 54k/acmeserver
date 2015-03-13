package com.acme.commons.utils.fsm;

public interface State<T> {

	/**
	 * Called when state machine enters this state
	 *
	 * @param fsm the calling state machine
	 */
	void enter(StateMachine<T> fsm);

	/**
	 * Called when state machine updates this state
	 *
	 * @param fsm the calling state machine
	 */
	void update(StateMachine<T> fsm, float deltaTime);

	/**
	 * Called when state machine exits this state
	 *
	 * @param fsm the calling state machine
	 */
	void exit(StateMachine<T> fsm);
}
