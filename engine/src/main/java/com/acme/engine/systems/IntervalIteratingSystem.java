package com.acme.engine.systems;

import com.acme.engine.aegis.Engine;
import com.acme.engine.aegis.Entity;
import com.acme.engine.aegis.Family;
import com.acme.engine.utils.ImmutableList;

public abstract class IntervalIteratingSystem extends IntervalSystem {

    private Family family;
    private ImmutableList<Entity> entities;

    public IntervalIteratingSystem(Family family, float interval) {
        this(family, interval, 0);
    }

    public IntervalIteratingSystem(Family family, float interval, int priority) {
        super(interval, priority);
        this.family = family;
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(family);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        entities = null;
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
