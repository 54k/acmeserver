package com.acme.commons.ashley;

import com.badlogic.ashley.core.*;

public abstract class ManagerSystem extends EntitySystem implements EntityListener {

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
    public void entityAdded(Entity entity) {
    }

    @Override
    public void entityRemoved(Entity entity) {
    }
}
