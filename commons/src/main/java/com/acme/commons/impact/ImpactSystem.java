package com.acme.commons.impact;

import com.acme.ecs.core.*;
import com.acme.ecs.utils.ImmutableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ImpactSystem<I extends Impact> extends EntitySystem {

    private Class<I> impactClass;
    private Family impactFamily;
    private ComponentMapper<I> impactCm;
    private ImpactListener listener;

    private ImmutableList<Entity> targets;
    private List<Entity> prevTargets;

    public ImpactSystem(Class<I> impactClass) {
        this(impactClass, 0);
    }

    public ImpactSystem(Class<I> impactClass, int priority) {
        super(priority);
        this.impactClass = impactClass;
        impactFamily = Family.all(impactClass).get();
        impactCm = ComponentMapper.getFor(impactClass);
        listener = new ImpactListener();
        prevTargets = new ArrayList<>(16);
    }

    @Override
    public void addedToEngine(Engine engine) {
        engine.addEntityListener(listener);
        targets = engine.getEntitiesFor(impactFamily);
    }

    @Override
    public void update(float deltaTime) {
        processAppliedImpacts();
        processRemovedImpacts();
        int size = targets.size();
        for (int i = 0; i < size; i++) {
            Entity target = targets.get(i);
            processImpact(impactCm.get(target), target, deltaTime);
        }
    }

    public void processAppliedImpacts() {
        for (int i = 0; i < targets.size(); i++) {
            Entity target = targets.get(i);
            if (!prevTargets.contains(target)) {
                listener.put(target, impactCm.get(target));
                prevTargets.add(target);
            }
        }
    }

    public void processRemovedImpacts() {
        for (int i = prevTargets.size() - 1; i >= 0; i--) {
            Entity target = prevTargets.get(i);
            if (!targets.contains(target)) {
                listener.remove(target);
                prevTargets.remove(i);
            }
        }
    }

    private void processImpact(I impact, Entity target, float deltaTime) {
        if (impact.interval > 0) {
            impact.timer -= deltaTime;
        }
        impactUpdated(impact, target, deltaTime);
        if (impact.timer <= 0) {
            if (impact.ticks > 0) {
                impact.ticks--;
            }
            impactTicked(impact, target);
            if (impact.isReady()) {
                impactReady(impact, target);
                target.removeComponent(impact.getClass());
            }
            impact.timer += impact.interval;
        }
    }

    public final I getImpact(Entity target) {
        return impactCm.get(target);
    }

    public final boolean hasImpact(Entity target) {
        return impactCm.has(target);
    }

    protected void impactApplied(I impact, Entity target) {
    }

    protected void impactStacked(I prevImpact, Entity target) {
    }

    protected void impactUpdated(I impact, Entity target, float deltaTime) {
    }

    protected void impactTicked(I impact, Entity target) {
    }

    protected void impactReady(I impact, Entity target) {
    }

    protected void impactRemoved(I impact, Entity target) {
    }

    private class ImpactListener implements EntityListener, ComponentListener {

        private Map<Entity, I> impactsByTarget = new HashMap<>();

        @Override
        public void entityAdded(Entity entity) {
        }

        @Override
        public void entityRemoved(Entity entity) {
            removeFromEngine(entity);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void componentAdded(Entity entity, Component component) {
            put(entity, (I) component);
        }

        @Override
        public void componentRemoved(Entity entity, Component component) {
        }

        public void put(Entity target, I impact) {
            I prevImpact = impactsByTarget.put(target, impact);
            if (prevImpact == null) {
                target.addComponentListener(impactClass, this);
                impactApplied(impact, target);
            } else {
                impactStacked(prevImpact, target);
            }
        }

        public void remove(Entity target) {
            I impact = impactsByTarget.remove(target);
            if (impact != null) {
                target.removeComponentListener(this);
                impactRemoved(impact, target);
            }
        }

        public void removeFromEngine(Entity target) {
            impactsByTarget.remove(target);
        }
    }
}
