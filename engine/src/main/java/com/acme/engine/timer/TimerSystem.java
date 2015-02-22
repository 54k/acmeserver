package com.acme.engine.timer;

import com.acme.engine.aegis.core.ComponentMapper;
import com.acme.engine.aegis.core.Entity;
import com.acme.engine.aegis.core.Family;
import com.acme.engine.aegis.systems.IteratingSystem;

public abstract class TimerSystem<T extends Timer> extends IteratingSystem {

    private ComponentMapper<T> timerCm;

    public TimerSystem(Class<T> timerClass) {
        this(timerClass, 0);
    }

    public TimerSystem(Class<T> timerClass, int priority) {
        super(Family.all(timerClass).get(), priority);
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
