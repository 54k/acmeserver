package com.acme.engine.systems;

import com.acme.engine.aegis.Engine;
import com.acme.engine.aegis.Entity;
import com.acme.engine.aegis.EntityListener;
import com.acme.engine.aegis.EntitySystem;
import com.acme.engine.aegis.Family;

public abstract class PassiveSystem extends EntitySystem implements EntityListener {

    private Family family;

    public PassiveSystem() {
        this(Family.all().get());
    }

    public PassiveSystem(Family family) {
        this.family = family;
        setDisabled(false);
    }

    @Override
    public void addedToEngine(Engine engine) {
        engine.addEntityListener(family, this);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        engine.removeEntityListener(family, this);
    }

    @Override
    public final void entityAdded(Entity entity) {
    }

    @Override
    public final void entityRemoved(Entity entity) {
    }
}
