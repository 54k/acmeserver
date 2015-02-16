package com.acme.engine.effects;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;

import java.util.HashSet;
import java.util.Set;

public abstract class ImpactSystem extends IteratingSystem {

    private Class<? extends Impact> impactClass;
    private ComponentMapper<? extends Impact> icm;

    private EffectManager effectManager;

    public ImpactSystem(Class<? extends Impact> impactClass) {
        this(impactClass, 0);
    }

    public ImpactSystem(Class<? extends Impact> impactClass, int priority) {
        super(EffectManager.effectListFamily, priority);
        this.impactClass = impactClass;
        icm = ComponentMapper.getFor(impactClass);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        effectManager = engine.getSystem(EffectManager.class);
        effectManager.impactListeners.put(impactClass, this);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        effectManager.impactListeners.remove(impactClass, this);
        effectManager = null;
    }

    @Override
    protected final void processEntity(Entity entity, float deltaTime) {
        Set<Entity> effects = new HashSet<>(EffectManager.effectListCm.get(entity).effects);
        effects.stream().filter(icm::has)
                .forEach(effect -> processEffect(effect, entity, deltaTime));
    }

    private void processEffect(Entity effect, Entity target, float deltaTime) {
        Effect effectComponent = EffectManager.effectCm.get(effect);
        effectUpdated(effect, target, deltaTime);
        if (effectComponent.tickInterval > 0) {
            effectComponent.timeToNextTick -= deltaTime;
        }
        if (effectComponent.timeToNextTick <= 0) {
            updateTicks(effectComponent);
            effectTicked(effect, target);
        }
        effectComponent.timeToNextTick += effectComponent.tickInterval;

        if (effectComponent.remainingTicks == 0) {
            effectReady(effect, target);
        }
    }

    protected void effectUpdated(Entity effect, Entity target, float deltaTime) {
    }

    private void updateTicks(Effect effect) {
        if (effect.remainingTicks > 0) {
            effect.remainingTicks--;
        }
    }

    protected void effectTicked(Entity effect, Entity target) {
    }

    protected void effectReady(Entity effect, Entity target) {
    }

    protected final void applyEffect(Entity effect, Entity target) {
        effectManager.applyEffect(effect, target);
    }

    protected final void removeEffect(Entity effect, Entity target) {
        effectManager.removeEffect(effect, target);
    }

    protected final boolean hasEffect(Entity effect, Entity target) {
        return effectManager.hasEffect(effect, target);
    }

    public void effectApplied(Entity effect, Entity target) {
    }

    public void effectRemoved(Entity effect, Entity target) {
    }

    public void effectStacked(Entity effect, Entity target) {
    }
}
