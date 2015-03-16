package com.acme.commons.utils.state;

public interface State<T> {

	/**
	 * Called when state machine enters this state.
	 *
	 * @param fsm the calling state machine
	 */
	void enter(StateMachine<T> fsm);

	/**
	 * Called when state machine updates this state.
	 *
	 * @param fsm the calling state machine
	 */
	void update(StateMachine<T> fsm, float deltaTime);

	/**
	 * Called when state machine exits this state.
	 *
	 * @param fsm the calling state machine
	 */
	void exit(StateMachine<T> fsm);

	/**
	 * Handles received message. The message is first routed to the current state. If the current state does not deal with the
	 * message, it's routed to the global state's message handler.
	 *
	 * @param fsm     the calling state machine
	 * @param message message
	 * @return true if message has been successfully handled; false otherwise.
	 */
	boolean handleMessage(StateMachine<T> fsm, Object message);
}
