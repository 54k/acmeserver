package com.acme.commons.utils.script;

import com.acme.ecs.core.Aspect;
import com.acme.ecs.core.Engine;
import com.acme.ecs.core.Entity;
import com.acme.ecs.core.EntityListener;
import com.acme.ecs.systems.AspectIteratingSystem;
import groovy.util.GroovyScriptEngine;

public class ScriptSystem extends AspectIteratingSystem implements EntityListener {

	private Engine engine;
	private GroovyScriptEngine gse;

	public ScriptSystem() {
		super(Aspect.all(ScriptComponent.class).get());
		initGSE();
	}

	private void initGSE() {
		try {
			gse = new GroovyScriptEngine("scripts/groovy");
			gse.loadScriptByName("TestScript");
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	@Override
	public void addedToEngine(Engine engine) {
		this.engine = engine;
		engine.addEntityListener(Aspect.all(ScriptComponent.class).get(), this);
	}

	@Override
	public void initialized() {
		super.initialized();
	}

	@Override
	public void entityAdded(Entity entity) {
		entity.getComponent(ScriptComponent.class).engine = this.engine;
	}

	@Override
	public void entityRemoved(Entity entity) {
		entity.getComponent(ScriptComponent.class).engine = null;
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		for (Behavior behavior : entity.getComponent(ScriptComponent.class).getBehaviors()) {
			behavior.update(deltaTime);
		}
	}
}
