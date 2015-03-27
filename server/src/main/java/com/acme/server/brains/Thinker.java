package com.acme.server.brains;

import com.acme.commons.utils.state.StateMachine;
import com.acme.ecs.core.Entity;

public class Thinker {

	private final StateMachine<Entity> stateMachine;

	public Thinker(Entity entity) {
		stateMachine = new StateMachine<>(entity);
	}
}
