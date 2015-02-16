package com.acme.engine.ashley.system;

import com.acme.engine.ashley.EntityEngine;
import com.acme.engine.ashley.EntityEngineListener;
import com.acme.engine.event.Event;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;

public abstract class ManagerSystem extends EntitySystem implements EntityEngineListener, EntityListener {

    private Family family;
    private EntityEngine engine;

    public ManagerSystem() {
        this(Family.all().get());
    }

    public ManagerSystem(Family family) {
        this.family = family;
        setProcessing(false);
    }

    @Override
    public void addedToEngine(EntityEngine engine) {
        this.engine = engine;
        engine.addEntityListener(this);
    }

    @Override
    public void removedFromEngine(EntityEngine engine) {
        this.engine = null;
        engine.removeEntityListener(this);
    }

    public <T extends Event> T post(Class<T> type) {
        return engine.post(type);
    }

    @Override
    public final void entityAdded(Entity entity) {
        if (family.matches(entity)) {
            entityAdded0(entity);
        }
    }

    protected void entityAdded0(Entity entity) {
    }

    @Override
    public final void entityRemoved(Entity entity) {
        if (family.matches(entity)) {
            entityRemoved0(entity);
        }
    }

    protected void entityRemoved0(Entity entity) {
    }
}
