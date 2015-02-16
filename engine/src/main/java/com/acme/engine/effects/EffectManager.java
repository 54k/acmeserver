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

public class EffectManager extends IteratingSystem {

    static final Family effectListFamily = Family.all(EffectList.class).get();
    static final ComponentMapper<EffectList> effectListCm = ComponentMapper.getFor(EffectList.class);
    static final ComponentMapper<Effect> effectCm = ComponentMapper.getFor(Effect.class);

    Map<Class<? extends Component>, ImpactSystem> impactListeners = new HashMap<>();

    public EffectManager() {
        this(0);
    }

    public EffectManager(int priority) {
        super(effectListFamily, priority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Set<Entity> effects = new HashSet<>(effectListCm.get(entity).effects);
        effects.stream()
                .filter(EffectManager::isReady)
                .forEach(effect -> removeEffect(effect, entity));
    }

    private static boolean isReady(Entity effect) {
        return effectCm.get(effect).isReady();
    }

    public void applyEffect(Entity effect, Entity target) {
        Set<Entity> effects = effectListCm.get(target).effects;
        if (effects.contains(effect)) {
            signalStacked(effect, target);
        } else {
            effects.add(effect);
            signalApplied(effect, target);
        }
    }

    public void removeEffect(Entity effect, Entity target) {
        if (effectListCm.get(target).effects.remove(effect)) {
            signalRemoved(effect, target);
        }
    }

    private void signalApplied(Entity effect, Entity target) {
        for (Component component : effect.getComponents()) {
            if (component instanceof Impact) {
                Class<? extends Component> impactClass = component.getClass();
                ImpactSystem impactListener = impactListeners.get(impactClass);
                impactListener.effectApplied(effect, target);
            }
        }
    }

    private void signalRemoved(Entity effect, Entity target) {
        for (Component component : effect.getComponents()) {
            if (component instanceof Impact) {
                Class<? extends Component> impactClass = component.getClass();
                ImpactSystem impactListener = impactListeners.get(impactClass);
                impactListener.effectRemoved(effect, target);
            }
        }
    }

    private void signalStacked(Entity effect, Entity target) {
        for (Component component : effect.getComponents()) {
            if (component instanceof Impact) {
                Class<? extends Component> impactClass = component.getClass();
                ImpactSystem impactListener = impactListeners.get(impactClass);
                impactListener.effectStacked(effect, target);
            }
        }
    }

    public boolean hasEffect(Entity effect, Entity target) {
        return effectListCm.get(target).effects.contains(effect);
    }
}
