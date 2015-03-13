package com.acme.ecs.systems;

import com.acme.ecs.core.*;

public abstract class PassiveSystem extends EntitySystem {

	public PassiveSystem() {
		setEnabled(false);
	}
}
