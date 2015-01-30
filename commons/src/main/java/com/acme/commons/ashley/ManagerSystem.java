package com.acme.commons.ashley;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;

public abstract class ManagerSystem extends EntitySystem implements EngineListener, EntityListener {

    public ManagerSystem() {
        setProcessing(false);
    }

    @Override
    public void addedToEngine(Engine engine) {
        //noinspection unchecked
        engine.addEntityListener(Family.all().get(), this);
        engine.addEntityListener(this);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        engine.removeEntityListener(this);
    }

    @Override
    public void addedToEngine(WiringEngine engine) {
    }

    @Override
    public void removedFromEngine(WiringEngine engine) {
    }

    @Override
    public void initialize() {
    }

    @Override
    public void entityAdded(Entity entity) {
    }

    @Override
    public void entityRemoved(Entity entity) {
    }
}
