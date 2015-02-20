package com.acme.engine.systems;

import com.acme.engine.aegis.*;

public abstract class PassiveSystem extends EntitySystem implements EntityListener {

    private Family family;

    public PassiveSystem() {
        this(Family.ALL);
    }

    public PassiveSystem(Family family) {
        this.family = family;
        setEnabled(false);
    }

    @Override
    public void addedToEngine(Engine engine) {
        engine.addEntityListener(family, this);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        engine.removeEntityListener(this);
    }

    @Override
    public final void entityAdded(Entity entity) {
    }

    @Override
    public final void entityRemoved(Entity entity) {
    }
}
