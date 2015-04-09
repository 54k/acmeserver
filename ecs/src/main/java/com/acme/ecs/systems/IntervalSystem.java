package com.acme.ecs.systems;

import com.acme.ecs.core.EntitySystem;

public abstract class IntervalSystem extends EntitySystem {

    private float interval;
    private float accumulator;

    public IntervalSystem(float interval) {
        this.interval = interval;
    }

    public IntervalSystem(float interval, int priority) {
        super(priority);
        this.interval = interval;
    }

    @Override
    public void update(float deltaTime) {
        accumulator += deltaTime;
        if (accumulator >= interval) {
            accumulator -= interval;
            updateInterval();
        }
    }

    protected abstract void updateInterval();
}
