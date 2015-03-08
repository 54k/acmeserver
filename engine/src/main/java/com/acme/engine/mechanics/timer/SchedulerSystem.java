package com.acme.engine.mechanics.timer;

import com.acme.engine.ecs.core.Engine;
import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.EntityListener;
import com.acme.engine.ecs.core.EntitySystem;
import com.acme.engine.ecs.utils.Pool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class SchedulerSystem extends EntitySystem implements EntityListener {

    private Scheduler scheduler;
    private Map<Entity, Scheduler> entitySchedulers;
    private SchedulerPool schedulerPool;

    public SchedulerSystem() {
        this(0);
    }

    public SchedulerSystem(int priority) {
        super(priority);
        scheduler = new Scheduler();
        entitySchedulers = new HashMap<>();
        schedulerPool = new SchedulerPool();
    }

    @Override
    public void addedToEngine(Engine engine) {
        engine.addEntityListener(this);
    }

    @Override
    public void entityAdded(Entity entity) {
        entitySchedulers.putIfAbsent(entity, schedulerPool.newObject());
    }

    @Override
    public void entityRemoved(Entity entity) {
        Scheduler s = entitySchedulers.remove(entity);
        if (s != null) {
            schedulerPool.free(s);
        }
    }

    @Override
    public void update(float deltaTime) {
        scheduler.update(deltaTime);
        for (Scheduler s : entitySchedulers.values()) {
            s.update(deltaTime);
        }
    }

    public PromiseTask<Void> schedule(Runnable task) {
        return scheduler.schedule(task);
    }

    public <T> PromiseTask<T> schedule(Callable<T> task) {
        return scheduler.schedule(task);
    }

    public PromiseTask<Void> schedule(Runnable task, float delay) {
        return scheduler.schedule(task, delay);
    }

    public <T> PromiseTask<T> schedule(Callable<T> task, float delay) {
        return scheduler.schedule(task, delay);
    }

    public PromiseTask<Void> schedule(Runnable task, float delay, float period) {
        return scheduler.schedule(task, delay, period);
    }

    public PromiseTask<Void> scheduleForEntity(Entity entity, Runnable task) {
        return getScheduler(entity).schedule(task);
    }

    public <T> PromiseTask<T> scheduleForEntity(Entity entity, Callable<T> task) {
        return getScheduler(entity).schedule(task);
    }

    public PromiseTask<Void> scheduleForEntity(Entity entity, Runnable task, float delay) {
        return getScheduler(entity).schedule(task, delay);
    }

    public <T> PromiseTask<T> scheduleForEntity(Entity entity, Callable<T> task, float delay) {
        return getScheduler(entity).schedule(task, delay);
    }

    public PromiseTask<Void> scheduleForEntity(Entity entity, Runnable task, float delay, float period) {
        return getScheduler(entity).schedule(task, delay, period);
    }

    private Scheduler getScheduler(Entity entity) {
        Scheduler scheduler = entitySchedulers.get(entity);
        if (scheduler == null) {
            scheduler = new Scheduler();
            entitySchedulers.put(entity, scheduler);
        }
        return scheduler;
    }

    private static class SchedulerPool extends Pool<Scheduler> {
        @Override
        protected Scheduler newObject() {
            return new Scheduler();
        }
    }
}
