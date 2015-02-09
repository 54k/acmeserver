package com.acme.engine.ashley.system;

import com.acme.engine.ashley.component.TimerComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public abstract class TimerSystem<T extends TimerComponent> extends IteratingSystem {

    private ComponentMapper<T> ccm;

    public TimerSystem(Class<T> cdClass) {
        this(cdClass, 0);
    }

    public TimerSystem(Class<T> cdClass, int priority) {
        super(Family.all(cdClass).get(), priority);
        ccm = ComponentMapper.getFor(cdClass);
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

    protected T getTimer(Entity entity) {
        return ccm.get(entity);
    }

    protected void timerTicked(Entity entity, float deltaTime) {
    }

    protected abstract void timerReady(Entity entity, float deltaTime);
}
