package com.acme.commons.timer;

import com.acme.ecs.core.ComponentMapper;
import com.acme.ecs.core.Entity;
import com.acme.ecs.core.Aspect;
import com.acme.ecs.systems.AspectIteratingSystem;

public abstract class TimerSystem<T extends Timer> extends AspectIteratingSystem {

    private ComponentMapper<T> timerCm;

    public TimerSystem(Class<T> timerClass) {
        this(timerClass, 0);
    }

    public TimerSystem(Class<T> timerClass, int priority) {
        super(Aspect.all(timerClass).get(), priority);
        timerCm = ComponentMapper.getFor(timerClass);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (shouldTickTimer(entity, deltaTime)) {
            processEntity0(entity, deltaTime);
        }
    }

    private void processEntity0(Entity entity, float deltaTime) {
        Timer timer = getTimer(entity);
        timer.decreaseTime(deltaTime);
        timerTicked(entity, deltaTime);
        if (timer.isReady()) {
            timerReady(entity, deltaTime);
            timer.refreshTimer();
        }
    }

    protected boolean shouldTickTimer(Entity entity, float deltaTime) {
        return true;
    }

    public T getTimer(Entity entity) {
        return timerCm.get(entity);
    }

    protected void timerTicked(Entity entity, float deltaTime) {
    }

    protected void timerReady(Entity entity, float deltaTime) {
    }
}
