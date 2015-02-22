package com.acme.engine.aegis.systems;

import com.acme.engine.aegis.core.Engine;
import com.acme.engine.aegis.core.Entity;
import com.acme.engine.aegis.core.EntitySystem;
import com.acme.engine.aegis.core.Family;
import com.acme.engine.aegis.utils.ImmutableList;

public abstract class IteratingSystem extends EntitySystem {

    private Family family;
    private ImmutableList<Entity> entities;

    /**
     * Instantiates a system that will iterate over the entities described by the Family.
     *
     * @param family The family of entities iterated over in this System
     */
    public IteratingSystem(Family family) {
        this(family, 0);
    }

    /**
     * Instantiates a system that will iterate over the entities described by the Family, with a specific priority.
     *
     * @param family   The family of entities iterated over in this System
     * @param priority The priority to execute this system with (lower means higher priority)
     */
    public IteratingSystem(Family family, int priority) {
        super(priority);
        this.family = family;
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(family);
    }

    @Override
    public void update(float deltaTime) {
        int size = entities.size();
        for (int i = 0; i < size; ++i) {
            processEntity(entities.get(i), deltaTime);
        }
    }

    /**
     * @return set of entities processed by the system
     */
    public ImmutableList<Entity> getEntities() {
        return entities;
    }

    /**
     * This method is called on every entity on every update call of the EntitySystem. Override this to implement your system's
     * specific processing.
     *
     * @param entity    The current Entity being processed
     * @param deltaTime The delta time between the last and current frame
     */
    protected abstract void processEntity(Entity entity, float deltaTime);
}
