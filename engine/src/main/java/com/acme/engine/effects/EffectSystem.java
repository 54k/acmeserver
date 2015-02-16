package com.acme.engine.effects;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import java.util.HashMap;
import java.util.Map;

public final class EffectSystem extends IteratingSystem implements EntityListener {

    static final Family effectFamily = Family.all(Effect.class).get();
    static final ComponentMapper<EffectList> effectListCm = ComponentMapper.getFor(EffectList.class);
    static final ComponentMapper<Effect> effectCm = ComponentMapper.getFor(Effect.class);

    Map<Class<? extends Component>, ImpactController> impactListeners = new HashMap<>();
    private Engine engine;

    public EffectSystem() {
        this(0);
    }

    public EffectSystem(int priority) {
        super(effectFamily, priority);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        this.engine = engine;
        engine.addEntityListener(this);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        this.engine = null;
        engine.removeEntityListener(this);
    }

    @Override
    public void entityAdded(Entity effect) {
        if (effectFamily.matches(effect)) {
            signalApplied(effect, effectCm.get(effect).target);
        }
    }

    @Override
    public void entityRemoved(Entity effect) {
        if (effectFamily.matches(effect)) {
            signalRemoved(effect, effectCm.get(effect).target);
        }
    }

    @Override
    protected void processEntity(Entity effect, float deltaTime) {
        processEffect(effect, effectCm.get(effect).target, deltaTime);
    }

    private void processEffect(Entity effect, Entity target, float deltaTime) {
        Effect effectComponent = effectCm.get(effect);
        signalUpdated(effect, target, deltaTime);
        if (effectComponent.tickInterval > 0) {
            effectComponent.timeToNextTick -= deltaTime;
        }
        if (effectComponent.timeToNextTick <= 0) {
            updateTicks(effectComponent);
            signalTicked(effect, target);
            signalIfReady(effectComponent, effect, target);
        }
    }

    private void updateTicks(Effect effect) {
        if (effect.remainingTicks > 0) {
            effect.remainingTicks--;
        }
    }

    private void signalIfReady(Effect effectComponent, Entity effect, Entity target) {
        if (effectComponent.remainingTicks == 0) {
            signalReady(effect, target);
            removeEffect(effectComponent.identity, target);
        }
    }

    public void applyEffect(Entity effect, Entity target) {
        Effect effectCmp = effectCm.get(effect);
        String identity = effectCmp.identity;
        if (hasEffect(identity, target)) {
            signalStacked(effect, target);
        } else {
            effectListCm.get(target).effectsByIdentity.put(identity, effect);
            effectCmp.target = target;
            engine.addEntity(effect);
        }
    }

    public void removeEffect(String identity, Entity target) {
        Entity effect = effectListCm.get(target).effectsByIdentity.remove(identity);
        if (effect != null) {
            engine.removeEntity(effect);
        }
    }

    private void signalApplied(Entity effect, Entity target) {
        for (Component component : effect.getComponents()) {
            if (component instanceof Impact) {
                Class<? extends Component> impactClass = component.getClass();
                ImpactController impactListener = impactListeners.get(impactClass);
                impactListener.applied(effect, target);
            }
        }
    }

    private void signalStacked(Entity effect, Entity target) {
        for (Component component : effect.getComponents()) {
            if (component instanceof Impact) {
                Class<? extends Component> impactClass = component.getClass();
                ImpactController impactListener = impactListeners.get(impactClass);
                impactListener.stacked(effect, target);
            }
        }
    }

    private void signalUpdated(Entity effect, Entity target, float deltaTime) {
        for (Component component : effect.getComponents()) {
            if (component instanceof Impact) {
                Class<? extends Component> impactClass = component.getClass();
                ImpactController impactListener = impactListeners.get(impactClass);
                impactListener.updated(effect, target, deltaTime);
            }
        }
    }

    private void signalTicked(Entity effect, Entity target) {
        for (Component component : effect.getComponents()) {
            if (component instanceof Impact) {
                Class<? extends Component> impactClass = component.getClass();
                ImpactController impactListener = impactListeners.get(impactClass);
                impactListener.ticked(effect, target);
            }
        }
    }

    private void signalReady(Entity effect, Entity target) {
        for (Component component : effect.getComponents()) {
            if (component instanceof Impact) {
                Class<? extends Component> impactClass = component.getClass();
                ImpactController impactListener = impactListeners.get(impactClass);
                impactListener.ready(effect, target);
            }
        }
    }

    private void signalRemoved(Entity effect, Entity target) {
        for (Component component : effect.getComponents()) {
            if (component instanceof Impact) {
                Class<? extends Component> impactClass = component.getClass();
                ImpactController impactListener = impactListeners.get(impactClass);
                impactListener.removed(effect, target);
            }
        }
    }

    public Entity getEffect(String identity, Entity target) {
        return effectListCm.get(target).effectsByIdentity.get(identity);
    }

    public Effect getEffectInfo(Entity effect) {
        return effectCm.get(effect);
    }

    public boolean hasEffect(String identity, Entity target) {
        return effectListCm.get(target).effectsByIdentity.containsKey(identity);
    }
}
