package com.acme.engine.impact;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Map;

public abstract class ImpactSystem<I extends Impact> extends EntitySystem implements EntityListener {

    private Family impactFamily;
    private ComponentMapper<I> impactCm;

    private ImpactListener impactListener;
    private Listener<Entity> impactAdded;

    private Array<Entity> prevTargets = new Array<>();
    private ImmutableArray<Entity> targets;

    public ImpactSystem(Class<I> impactClass) {
        this(impactClass, 0);
    }

    public ImpactSystem(Class<I> impactClass, int priority) {
        super(priority);
        impactFamily = Family.all(impactClass).get();
        impactCm = ComponentMapper.getFor(impactClass);

        impactListener = new ImpactListener();
        impactAdded = (signal, entity) -> {
            I impact = impactCm.get(entity);
            if (impact != null) {
                impactListener.put(entity, impact);
            }
        };
    }

    @Override
    public void addedToEngine(Engine engine) {
        engine.addEntityListener(this);
        targets = engine.getEntitiesFor(impactFamily);
        fillPrevTargets();
    }

    @Override
    public void removedFromEngine(Engine engine) {
        engine.removeEntityListener(this);
        impactListener.clear();
        targets = null;
        prevTargets.clear();
    }

    @Override
    public void entityAdded(Entity entity) {
    }

    @Override
    public void entityRemoved(Entity entity) {
        impactListener.removeFromEngine(entity);
    }

    public boolean hasImpact(Entity target) {
        return impactCm.has(target);
    }

    @Override
    public void update(float deltaTime) {
        signalApplied();
        signalRemoved();
        for (int i = 0; i < targets.size(); i++) {
            Entity target = targets.get(i);
            processImpact(impactCm.get(target), target, deltaTime);
        }
        fillPrevTargets();
    }

    private void fillPrevTargets() {
        prevTargets.clear();
        targets.forEach(prevTargets::add);
    }

    private void signalApplied() {
        for (Entity target : targets) {
            if (!prevTargets.contains(target, true)) {
                impactListener.put(target, getImpact(target));
            }
        }
    }

    private void signalRemoved() {
        for (Entity prevTarget : prevTargets) {
            if (!targets.contains(prevTarget, true)) {
                impactListener.remove(prevTarget);
            }
        }
    }

    protected I getImpact(Entity target) {
        return impactCm.get(target);
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
                target.remove(impact.getClass());
            }
            impact.timer += impact.interval;
        }
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

    private class ImpactListener {

        private Map<Entity, I> impacts = new HashMap<>();

        public void put(Entity target, I impact) {
            I prevImpact = impacts.put(target, impact);
            if (prevImpact == null) {
                target.componentAdded.add(impactAdded);
                impactApplied(impact, target);
            } else {
                impactStacked(prevImpact, target);
            }
        }

        public void removeFromEngine(Entity target) {
            impacts.remove(target);
            prevTargets.removeValue(target, true);
        }

        public void remove(Entity target) {
            I impact = impacts.remove(target);
            if (impact == null) {
                throw new NullPointerException();
            }
            target.componentAdded.remove(impactAdded);
            impactRemoved(impact, target);
        }

        public void clear() {
            impacts.clear();
        }
    }
}
