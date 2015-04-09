package com.acme.commons.utils.script;

import com.acme.ecs.core.Component;
import com.acme.ecs.core.Engine;
import com.acme.ecs.core.Entity;
import com.acme.ecs.events.Event;
import com.acme.ecs.events.EventListener;
import com.acme.ecs.events.Signal;
import com.acme.ecs.utils.reflection.ClassReflection;

import java.util.HashMap;
import java.util.Map;

public class ScriptComponent extends Component {

	Engine engine;

	private final Entity entity;
	private final Map<Class<? extends Behavior>, Behavior> behaviorsByClass;

	private Map<Class<?>, Signal<?>> signals;
	private Map<Class<? extends EventListener>, Event<? extends EventListener>> events;

	public ScriptComponent(Entity entity) {
		this.entity = entity;
		behaviorsByClass = new HashMap<>();
		signals = new HashMap<>();
		events = new HashMap<>();
	}

	public void addBehavior(Class<? extends Behavior> behaviorClass) {
		Behavior behavior = ClassReflection.newInstance(behaviorClass);
		behavior.owner = this;
		behaviorsByClass.put(behaviorClass, behavior);
	}

	public void removeBehavior(Class<? extends Behavior> behaviorClass) {
		Behavior behavior = behaviorsByClass.remove(behaviorClass);
		if (behavior != null) {
			behavior.owner = null;
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends Behavior> T getBehavior(Class<T> behaviorClass) {
		return (T) behaviorsByClass.get(behaviorClass);
	}

	public boolean hasBehavior(Class<? extends Behavior> behaviorClass) {
		return behaviorsByClass.containsKey(behaviorClass);
	}

	public Iterable<? extends Behavior> getBehaviors() {
		return behaviorsByClass.values();
	}

	@SuppressWarnings("unchecked")
	public <T> Signal<T> signal(Class<T> type) {
		Signal<?> signal = signals.get(type);
		if (signal == null) {
			signal = new Signal<>();
			signals.put(type, signal);
		}
		return (Signal<T>) signal;
	}

	@SuppressWarnings("unchecked")
	public <T extends EventListener> Event<T> event(Class<T> listenerType) {
		Event<? extends EventListener> event = events.get(listenerType);
		if (event == null) {
			event = new Event<>(listenerType);
			events.put(listenerType, event);
		}
		return (Event<T>) event;
	}
}
