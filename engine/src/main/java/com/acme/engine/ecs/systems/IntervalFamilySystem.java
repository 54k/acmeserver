package com.acme.engine.ecs.systems;

import com.acme.engine.ecs.core.Engine;
import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.Family;
import com.acme.engine.ecs.utils.ImmutableList;

public abstract class IntervalFamilySystem extends IntervalSystem {

    private Family family;
    private ImmutableList<Entity> entities;

    public IntervalFamilySystem(Family family, float interval) {
        this(family, interval, 0);
    }

    public IntervalFamilySystem(Family family, float interval, int priority) {
        super(interval, priority);
        this.family = family;
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(family);
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
