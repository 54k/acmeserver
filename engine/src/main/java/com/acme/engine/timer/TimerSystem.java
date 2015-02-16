package com.acme.engine.timer;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public abstract class TimerSystem<T extends TimerComponent> extends IteratingSystem {

    private ComponentMapper<T> ccm;

    public TimerSystem(Class<T> timerClass) {
        this(timerClass, 0);
    }

    public TimerSystem(Class<T> timerClass, int priority) {
        super(Family.all(timerClass).get(), priority);
        ccm = ComponentMapper.getFor(timerClass);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (shouldTickTimer(entity, deltaTime)) {
            processEntity0(entity, deltaTime);
        }
    }

    private void processEntity0(Entity entity, float deltaTime) {
        TimerComponent timerComponent = getTimer(entity);
        timerComponent.decreaseTime(deltaTime);
        timerTicked(entity, deltaTime);
        if (timerComponent.isReady()) {
            timerReady(entity, deltaTime);
            timerComponent.refreshTimer();
        }
    }

    protected boolean shouldTickTimer(Entity entity, float deltaTime) {
        return true;
    }

    public T getTimer(Entity entity) {
        return ccm.get(entity);
    }

    protected void timerTicked(Entity entity, float deltaTime) {
    }

    protected void timerReady(Entity entity, float deltaTime) {
    }
}