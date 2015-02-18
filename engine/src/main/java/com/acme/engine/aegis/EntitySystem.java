package com.acme.engine.aegis;

/**
 * Abstract class for processing sets of {@link Entity} objects.
 *
 * @author Stefan Bachmann
 */
public abstract class EntitySystem {
    /**
     * Use this to set the priority of the system. Lower means it'll get executed first.
     */
    public int priority;

    private boolean processing;

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
        this.processing = true;
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
     * Called when this EntitySystem is removed from an {@link Engine}.
     *
     * @param engine The {@link Engine} the system was removed from.
     */
    public void removedFromEngine(Engine engine) {
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
    public boolean checkProcessing() {
        return processing;
    }

    /**
     * Sets whether or not the system should be processed by the {@link Engine}.
     */
    public void setProcessing(boolean processing) {
        this.processing = processing;
    }
}
