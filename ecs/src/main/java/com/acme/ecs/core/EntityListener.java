package com.acme.ecs.core;

public interface EntityListener {
    /**
     * Called whenever an {@link Entity} is added to {@link Engine} or a specific {@link Aspect} See
     * {@link Engine#addEntityListener(EntityListener)} and {@link Engine#addEntityListener(Aspect, EntityListener)}
     */
    void entityAdded(Entity entity);

    /**
     * Called whenever an {@link Entity} is removed from {@link Engine} or a specific {@link Aspect} See
     * {@link Engine#addEntityListener(EntityListener)} and {@link Engine#addEntityListener(Aspect, EntityListener)}
     */
    void entityRemoved(Entity entity);
}
