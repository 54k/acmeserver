package com.acme.ecs.core;

import com.acme.ecs.events.Event;
import com.acme.ecs.events.EventListener;
import com.acme.ecs.events.Signal;

/**
 * Abstract class for disabled sets of {@link Entity} objects.
 */
public abstract class EntitySystem {
    /**
     * Use this to set the priority of the system. Lower means it'll get executed first.
     */
    int priority;
    private boolean enabled;

    Engine engine;

    /**
     * Default constructor that will initialise an EntitySystem with priority 0.
     */
    public EntitySystem() {
        this(0);
    }

    /**
     * Initialises the EntitySystem with the priority specified.
     *
     * @param priority The priority to execute this system with (lower means higher priority).
     */
    public EntitySystem(int priority) {
        this.priority = priority;
        this.enabled = true;
    }

    public void initialized() {
    }

    /**
     * Called when this EntitySystem is added to an {@link Engine}.
     *
     * @param engine The {@link Engine} this system was added to.
     */
    public void addedToEngine(Engine engine) {
    }

    /**
     * The update method called every tick.
     *
     * @param deltaTime The time passed since last frame in seconds.
     */
    public void update(float deltaTime) {
    }

    /**
     * @return Whether or not the system should be processed.
     */
    public final boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether or not the system should be processed by the {@link Engine}.
     */
    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public <T> Signal<T> signal(Class<T> type) {
        return engine.signal(type);
    }

    public <T extends EventListener> Event<T> event(Class<T> listenerType) {
        return engine.event(listenerType);
    }
}
