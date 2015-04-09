package com.acme.commons.utils.scheduler;

import com.acme.commons.utils.promise.Deferred;
import com.acme.commons.utils.promise.PromiseHandler;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executors;

public class DeltaTimeScheduler {

    private final Queue<DeferredTask<?>> taskQueue = new PriorityQueue<>();
    private final Queue<DeferredTask<?>> tasksToRun = new PriorityQueue<>();

    /**
     * Updates this scheduler
     *
     * @param deltaTime time delta since last update
     */
    public void update(float deltaTime) {
        tasksToRun.addAll(taskQueue);
        taskQueue.clear();

        while (!tasksToRun.isEmpty()) {
            tasksToRun.poll().run(deltaTime);
        }
    }

    /**
     * Submits the given task with no delay.
     *
     * @param task task
     */
    public PromiseTask<Void> schedule(Runnable task) {
        return schedule(task, 0);
    }

    /**
     * Submits the given task with no delay.
     *
     * @param task task
     */
    public <T> PromiseTask<T> schedule(Callable<T> task) {
        return schedule(task, 0);
    }

    /**
     * Submits the given task with the given delay.
     *
     * @param task  task
     * @param delay delay
     */
    public PromiseTask<Void> schedule(Runnable task, float delay) {
        return schedule(Executors.callable(task, null), delay);
    }

    /**
     * Submits the given task with the given delay.
     *
     * @param task  task
     * @param delay delay
     */
    public <T> PromiseTask<T> schedule(Callable<T> task, float delay) {
        return submitTask(task, delay, -1);
    }

    /**
     * Submits the given task with the given delay and period.
     *
     * @param task   task
     * @param delay  delay
     * @param period period
     */
    public PromiseTask<Void> schedule(Runnable task, float delay, float period) {
        return submitTask(Executors.callable(task, null), delay, period);
    }

    private <T> PromiseTask<T> submitTask(Callable<T> task, float delay, float period) {
        DeferredTask<T> t = new DeferredTask<>(task, delay, period, taskQueue);
        taskQueue.add(t);
        return t;
    }

    /**
     * Clears task queue, all remaining tasks will be cancelled
     */
    public void clear() {
        while (!taskQueue.isEmpty()) {
            taskQueue.poll().cancel();
        }
    }

    private static final class DeferredTask<T> extends Deferred<T, Throwable> implements PromiseTask<T>, Comparable<DeferredTask<?>> {

        private final Callable<T> task;
        private float atAge;
        private float age;
        private final float period;
        private final Queue<DeferredTask<?>> taskQueue;

        private boolean cancelled;

        DeferredTask(Callable<T> task, float delay, float period, Queue<DeferredTask<?>> taskQueue) {
            this.task = task;
            this.atAge += delay;
            this.period = period;
            this.taskQueue = taskQueue;
        }

        void run(float deltaTime) {
            if (cancelled) {
                return;
            }

            age += deltaTime;
            if (age < atAge) {
                taskQueue.add(this);
                return;
            }

            try {
                T result = task.call();
                if (period > -1) {
                    atAge += period;
                    taskQueue.add(this);
                } else {
                    resolve(result);
                }
            } catch (Throwable t) {
                reject(t);
            }
        }

        @Override
        public PromiseTask<T> done(PromiseHandler<T> resolveHandler) {
            super.done(resolveHandler);
            return this;
        }

        @Override
        public PromiseTask<T> fail(PromiseHandler<Throwable> rejectHandler) {
            super.fail(rejectHandler);
            return this;
        }

        @Override
        public void cancel() {
            checkFulfilled();
            cancelled = true;
            reject(new CancellationException());
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public int compareTo(DeferredTask<?> o) {
            if (this == o) {
                return 0;
            }
            if (atAge == o.atAge) {
                return 0;
            }
            return atAge > o.atAge ? 1 : -1;
        }
    }
}
