package com.acme.engine.ecs.core;

import com.acme.engine.ecs.utils.Pool;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

class Scheduler implements Pool.Disposable {

    private final Queue<DeferredTask> scheduledTasks = new LinkedList<>();
    private final Queue<DeferredTask> tasksToRun = new LinkedList<>();

    public void update(float deltaTime) {
        tasksToRun.addAll(scheduledTasks);
        scheduledTasks.clear();
        while (!tasksToRun.isEmpty()) {
            DeferredTask task = tasksToRun.poll();
            executeTask(deltaTime, task);
        }
    }

    private void executeTask(float deltaTime, DeferredTask<?> task) {
        if (!task.cancelled) {
            task.age += deltaTime;
            if (task.age >= task.atAge) {
                task.execute();
            }
        }
    }

    public PromiseTask<Void> schedule(Runnable task) {
        return schedule(task, 0);
    }

    public <T> PromiseTask<T> schedule(Callable<T> task) {
        return schedule(task, 0);
    }

    public PromiseTask<Void> schedule(Runnable task, float delay) {
        return schedule(Executors.callable(task, null), delay);
    }

    public <T> PromiseTask<T> schedule(Callable<T> task, float delay) {
        DeferredTask<T> t = new DeferredTask<>(task, delay);
        scheduledTasks.add(t);
        return t;
    }

    @Override
    public void dispose() {
        scheduledTasks.clear();
    }
}
