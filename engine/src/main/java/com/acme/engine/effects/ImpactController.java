package com.acme.engine.effects;

import com.acme.engine.ashley.system.ManagerSystem;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

public abstract class ImpactController extends ManagerSystem {

    private Class<? extends Impact> impactClass;
    private EffectSystem effectSystem;

    public ImpactController(Class<? extends Impact> impactClass) {
        this.impactClass = impactClass;
    }

    @Override
    public void addedToEngine(Engine engine) {
        effectSystem = engine.getSystem(EffectSystem.class);
        effectSystem.impactListeners.put(impactClass, this);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        effectSystem.impactListeners.remove(impactClass, this);
        effectSystem = null;
    }

    public void applied(Entity effect, Entity target) {
    }

    public void stacked(Entity effect, Entity target) {
    }

    public void updated(Entity effect, Entity target, float deltaTime) {
    }

    public void ticked(Entity effect, Entity target) {
    }

    public void ready(Entity effect, Entity target) {
    }

    public void removed(Entity effect, Entity target) {
    }
}
