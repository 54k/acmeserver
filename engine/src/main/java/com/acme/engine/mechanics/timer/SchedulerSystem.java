package com.acme.engine.mechanics.timer;

import com.acme.engine.ecs.core.Engine;
import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.EntityListener;
import com.acme.engine.ecs.core.EntitySystem;

import java.util.HashMap;
import java.util.Map;

public final class SchedulerSystem extends EntitySystem implements EntityListener {

    private final Scheduler globalScheduler;
    private final Map<Entity, Scheduler> schedulersByEntity;

    public SchedulerSystem() {
        this(0);
    }

    public SchedulerSystem(int priority) {
        super(priority);
        globalScheduler = new Scheduler();
        schedulersByEntity = new HashMap<>();
    }

    @Override
    public void addedToEngine(Engine engine) {
        engine.addEntityListener(this);
    }

    @Override
    public void entityAdded(Entity entity) {
    }

    @Override
    public void entityRemoved(Entity entity) {
        schedulersByEntity.remove(entity);
    }

    @Override
    public void update(float deltaTime) {
        globalScheduler.update(deltaTime);
        for (Scheduler scheduler : schedulersByEntity.values()) {
            scheduler.update(deltaTime);
        }
    }

    public Scheduler.Cancellable schedule(Scheduler.Task task) {
        return globalScheduler.schedule(task);
    }

    public Scheduler.Cancellable schedule(Scheduler.Task task, float delay) {
        return globalScheduler.schedule(task, delay);
    }

    public Scheduler.Cancellable schedule(Scheduler.Task task, float delay, float period) {
        return globalScheduler.schedule(task, delay, period);
    }

    public Scheduler.Cancellable scheduleForEntity(Entity entity, Scheduler.Task task) {
        return scheduleForEntity(entity, task, 0);
    }

    public Scheduler.Cancellable scheduleForEntity(Entity entity, Scheduler.Task task, float delay) {
        return scheduleForEntity(entity, task, delay, 0);
    }

    public Scheduler.Cancellable scheduleForEntity(Entity entity, Scheduler.Task task, float delay, float period) {
        Scheduler scheduler = getSchedulerFor(entity);
        return scheduler.schedule(task, delay, period);
    }

    private Scheduler getSchedulerFor(Entity entity) {
        Scheduler scheduler = schedulersByEntity.get(entity);
        if (scheduler == null) {
            scheduler = new Scheduler();
            schedulersByEntity.put(entity, scheduler);
        }
        return scheduler;
    }
}
