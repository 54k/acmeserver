package com.acme.commons.utils.script;

public abstract class Behavior {

	ScriptComponent owner;

	public final ScriptComponent getOwner() {
		return owner;
	}

	public void update(float deltaTime) {
	}
}
