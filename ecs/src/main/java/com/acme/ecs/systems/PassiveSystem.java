package com.acme.ecs.systems;

import com.acme.ecs.core.*;

public abstract class PassiveSystem extends EntitySystem implements EntityListener {

    private Aspect aspect;

    public PassiveSystem() {
        this(Aspect.ALL);
    }

    public PassiveSystem(Aspect aspect) {
        this.aspect = aspect;
        setEnabled(false);
    }

    @Override
    public void addedToEngine(Engine engine) {
        engine.addEntityListener(aspect, this);
    }

    @Override
    public void entityAdded(Entity entity) {
    }

    @Override
    public void entityRemoved(Entity entity) {
    }
}
