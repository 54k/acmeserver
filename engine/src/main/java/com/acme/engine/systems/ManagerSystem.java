package com.acme.engine.systems;

import com.acme.engine.aegis.Engine;
import com.acme.engine.aegis.Entity;
import com.acme.engine.aegis.EntityListener;
import com.acme.engine.aegis.EntitySystem;
import com.acme.engine.aegis.Family;
import com.acme.engine.event.Event;

public abstract class ManagerSystem extends EntitySystem implements EntityListener {

    private Family family;
    private Engine engine;

    public ManagerSystem() {
        this(Family.all().get());
    }

    public ManagerSystem(Family family) {
        this.family = family;
        setProcessing(false);
    }

    @Override
    public void addedToEngine(Engine engine) {
        this.engine = engine;
        engine.addEntityListener(this);
    }

    @Override
    public void removedFromEngine(Engine engine) {
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
