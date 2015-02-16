package com.acme.engine.effects;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class EffectSystem extends IteratingSystem {

    static final Family effectListFamily = Family.all(EffectList.class).get();
    static final ComponentMapper<EffectList> effectListCm = ComponentMapper.getFor(EffectList.class);
    static final ComponentMapper<Effect> effectCm = ComponentMapper.getFor(Effect.class);

    Map<Class<? extends Component>, ImpactController> impactListeners = new HashMap<>();

    public EffectSystem() {
        this(0);
    }

    public EffectSystem(int priority) {
        super(effectListFamily, priority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        processEffectList(entity, deltaTime);
    }

    private void processEffectList(Entity entity, float deltaTime) {
        Set<Entity> effects = new HashSet<>(effectListCm.get(entity).effectsByIdentity.values());
        effects.stream().forEach(effect -> processEffect(effect, entity, deltaTime));
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
        String identity = effectCm.get(effect).identity;
        if (hasEffect(identity, target)) {
            signalStacked(effect, target);
        } else {
            effectListCm.get(target).effectsByIdentity.put(identity, effect);
            signalApplied(effect, target);
        }
    }

    public void removeEffect(String identity, Entity target) {
        Entity effect = effectListCm.get(target).effectsByIdentity.remove(identity);
        if (effect != null) {
            signalRemoved(effect, target);
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
