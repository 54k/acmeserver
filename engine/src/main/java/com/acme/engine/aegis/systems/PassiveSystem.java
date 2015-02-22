package com.acme.engine.aegis.systems;

import com.acme.engine.aegis.core.*;

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
    public void entityAdded(Entity entity) {
    }

    @Override
    public void entityRemoved(Entity entity) {
    }
}
