package com.acme.engine.aegis;

public interface EntityListener {
    /**
     * Called whenever an {@link Entity} is added to {@link Engine} or a specific {@link Family} See
     * {@link Engine#addEntityListener(EntityListener)} and {@link Engine#addEntityListener(Family, EntityListener)}
     *
     * @param entity
     */
    public void entityAdded(Entity entity);

    /**
     * Called whenever an {@link Entity} is removed from {@link Engine} or a specific {@link Family} See
     * {@link Engine#addEntityListener(EntityListener)} and {@link Engine#addEntityListener(Family, EntityListener)}
     *
     * @param entity
     */
    public void entityRemoved(Entity entity);
}
