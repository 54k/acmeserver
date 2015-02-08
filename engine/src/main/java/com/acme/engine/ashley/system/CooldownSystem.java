package com.acme.engine.ashley.system;

import com.acme.engine.ashley.component.CooldownComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public abstract class CooldownSystem<T extends CooldownComponent> extends IteratingSystem {

    private ComponentMapper<T> ccm;

    public CooldownSystem(Class<T> cdClass) {
        this(cdClass, 0);
    }

    public CooldownSystem(Class<T> cdClass, int priority) {
        super(Family.all(cdClass).get(), priority);
        ccm = ComponentMapper.getFor(cdClass);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (shouldTickCooldown(entity, deltaTime)) {
            processEntity0(entity, deltaTime);
        }
    }

    private void processEntity0(Entity entity, float deltaTime) {
        CooldownComponent cooldownComponent = getComponent(entity);
        cooldownComponent.decreaseCooldown(deltaTime);
        cooldownTicked(entity, deltaTime);
        if (cooldownComponent.isReady()) {
            cooldownReady(entity, deltaTime);
            cooldownComponent.refreshCooldown();
        }
    }

    protected boolean shouldTickCooldown(Entity entity, float deltaTime) {
        return true;
    }

    protected T getComponent(Entity entity) {
        return ccm.get(entity);
    }

    protected void cooldownTicked(Entity entity, float deltaTime) {
    }

    protected abstract void cooldownReady(Entity entity, float deltaTime);
}
