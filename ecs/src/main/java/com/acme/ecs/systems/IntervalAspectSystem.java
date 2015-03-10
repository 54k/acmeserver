package com.acme.ecs.systems;

import com.acme.ecs.core.Engine;
import com.acme.ecs.core.Entity;
import com.acme.ecs.core.Aspect;
import com.acme.ecs.utils.ImmutableList;

public abstract class IntervalAspectSystem extends IntervalSystem {

    private Aspect aspect;
    private ImmutableList<Entity> entities;

    public IntervalAspectSystem(Aspect aspect, float interval) {
        this(aspect, interval, 0);
    }

    public IntervalAspectSystem(Aspect aspect, float interval, int priority) {
        super(interval, priority);
        this.aspect = aspect;
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(aspect);
    }

    @Override
    protected void updateInterval() {
        int size = entities.size();
        for (int i = 0; i < size; i++) {
            processEntity(entities.get(i));
        }
    }

    protected abstract void processEntity(Entity entity);
}
