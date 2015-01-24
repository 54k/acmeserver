package com.acme.commons.ashley;

import com.acme.commons.application.Context;
import com.badlogic.ashley.core.*;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.HashMap;
import java.util.Map;

public class WiringEngine extends Engine {

    private final Signal<Object> injectSignal = new Signal<>();
    private final Map<EntitySystem, Listener<Object>> injectionListeners = new HashMap<>();

    private final Injector injector;
    private final Engine engine;

    private boolean initialized;

    public WiringEngine(Context context, Engine engine) {
        this.engine = engine;
        injector = new Injector(context, this);
    }

    @Override
    public void addEntity(Entity entity) {
        engine.addEntity(entity);
    }

    @Override
    public void removeEntity(Entity entity) {
        engine.removeEntity(entity);
    }

    @Override
    public void removeAllEntities() {
        engine.removeAllEntities();
    }

    @Override
    public void addSystem(EntitySystem system) {
        engine.addSystem(system);
        Listener<Object> listener = ($1, $2) -> wireObject(system);
        injectionListeners.put(system, listener);
        injectSignal.add(listener);
        if (initialized) {
            injectSignal.dispatch(null);
        }
    }

    @Override
    public void removeSystem(EntitySystem system) {
        engine.removeSystem(system);
        cleanObject(system);
        Listener<Object> listener = injectionListeners.get(system);
        injectSignal.remove(listener);
        injectSignal.dispatch(null);
    }

    @Override
    public <T extends EntitySystem> T getSystem(Class<T> systemType) {
        return engine.getSystem(systemType);
    }

    @Override
    public ImmutableArray<EntitySystem> getSystems() {
        return engine.getSystems();
    }

    @Override
    public ImmutableArray<Entity> getEntitiesFor(Family family) {
        return engine.getEntitiesFor(family);
    }

    @Override
    public void addEntityListener(EntityListener listener) {
        engine.addEntityListener(listener);
    }

    @Override
    public void addEntityListener(Family family, EntityListener listener) {
        engine.addEntityListener(family, listener);
    }

    @Override
    public void removeEntityListener(EntityListener listener) {
        engine.removeEntityListener(listener);
    }

    @Override
    public void update(float deltaTime) {
        initialize();
        engine.update(deltaTime);
    }

    public void initialize() {
        if (!initialized) {
            initialized = true;
            injectSignal.dispatch(null);
        }
    }

    public void wireObject(Object o) {
        injector.wireObject(o);
    }

    public void cleanObject(Object o) {
        injector.cleanObject(o);
    }

    public <T extends Engine> T unwrap(Class<T> engineClass) {
        return engineClass.cast(engine);
    }
}
