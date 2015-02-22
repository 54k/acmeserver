package com.acme.engine.mechanics.impact;

import com.acme.engine.ecs.core.*;
import com.acme.engine.ecs.utils.ImmutableList;

import java.util.HashMap;
import java.util.Map;

public abstract class ImpactSystem<I extends Impact> extends EntitySystem {

    private Class<I> impactClass;
    private Family impactFamily;
    private ComponentMapper<I> impactCm;
    private ImpactListener listener;

    private ImmutableList<Entity> entities;

    public ImpactSystem(Class<I> impactClass) {
        this(impactClass, 0);
    }

    public ImpactSystem(Class<I> impactClass, int priority) {
        super(priority);
        this.impactClass = impactClass;
        impactFamily = Family.all(impactClass).get();
        impactCm = ComponentMapper.getFor(impactClass);
        listener = new ImpactListener();
    }

    @Override
    public void addedToEngine(Engine engine) {
        engine.addEntityListener(listener);
        entities = engine.getEntitiesFor(impactFamily);
    }

    @Override
    public void update(float deltaTime) {
        for (int i = 0; i < entities.size(); i++) {
            Entity target = entities.get(i);
            processImpact(impactCm.get(target), target, deltaTime);
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
                target.remove(impact.getClass());
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
            put(entity, getImpact(entity));
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

        public void put(Entity target, I impact) {
            I prevImpact = impactsByTarget.put(target, impact);
            if (prevImpact == null) {
                target.addComponentListener(impactClass, this);
                impactApplied(impact, target);
            } else {
                impactStacked(prevImpact, target);
            }
        }

        @Override
        public void componentRemoved(Entity entity, Component component) {
            remove(entity);
        }

        public void remove(Entity target) {
            I impact = impactsByTarget.remove(target);
            if (impact == null) {
                throw new NullPointerException();
            }
            target.removeComponentListener(this);
            impactRemoved(impact, target);
        }

        public void removeFromEngine(Entity target) {
            impactsByTarget.remove(target);
        }
    }
}
