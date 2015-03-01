package com.acme.engine.mechanics.timer;

import com.acme.engine.ecs.core.*;
import com.acme.engine.ecs.utils.ImmutableList;

@Wire
public final class SchedulerSystem extends EntitySystem {

    private static final Family schedulerFamily = Family.all(SchedulerHolder.class).get();

    private ComponentMapper<SchedulerHolder> schedulerCm;
    private final Scheduler globalScheduler;
    private ImmutableList<Entity> entities;

    public SchedulerSystem() {
        this(0);
    }

    public SchedulerSystem(int priority) {
        super(priority);
        globalScheduler = new Scheduler();
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(schedulerFamily);
    }

    @Override
    public void update(float deltaTime) {
        globalScheduler.update(deltaTime);
        for (Entity entity : entities) {
            schedulerCm.get(entity).scheduler.update(deltaTime);
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
        Scheduler scheduler = schedulerCm.get(entity).scheduler;
        return scheduler.schedule(task, delay, period);
    }
}
