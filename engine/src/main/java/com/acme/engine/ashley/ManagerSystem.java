package com.acme.engine.ashley;

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
        engine.addEntityListener(family, this);
        engine.addEntityListener(this);
    }

    @Override
    public void removedFromEngine(EntityEngine engine) {
        engine.removeEntityListener(this);
        this.engine = null;
    }

    public <T extends Event> T post(Class<T> type) {
        return engine.post(type);
    }

    @Override
    public void entityAdded(Entity entity) {
    }

    @Override
    public void entityRemoved(Entity entity) {
    }
}
