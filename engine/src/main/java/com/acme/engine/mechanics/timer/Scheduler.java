package com.acme.engine.mechanics.timer;

import java.util.PriorityQueue;
import java.util.Queue;

public class Scheduler {

    private static final long ORIGIN_NANOS = System.nanoTime();

    private final Queue<Task> scheduledTasks;
    private final Queue<Task> tasksToRun;
    private long lastNanos;

    public Scheduler() {
        scheduledTasks = new PriorityQueue<>();
        tasksToRun = new PriorityQueue<>();
        lastNanos = System.nanoTime();
    }

    static long nanos() {
        return System.nanoTime() - ORIGIN_NANOS;
    }

    public void update(float deltaTime) {
        if (scheduledTasks.isEmpty()) {
            return;
        }
        long nanos = nanos();

        tasksToRun.addAll(scheduledTasks);
        scheduledTasks.clear();
        while (!tasksToRun.isEmpty()) {
            tasksToRun.poll().run(deltaTime);
        }
    }

    public Cancellable schedule(Task task) {
        return schedule(task, 0);
    }

    public Cancellable schedule(Task task, float delay) {
        return schedule(task, delay, 0);
    }

    public Cancellable schedule(Task task, float delay, float period) {
        ScheduledTask t = new ScheduledTask(task, delay, period, scheduledTasks);
        scheduledTasks.add(t);
        return t;
    }

    private static final class SchedulerRecord {

        private final long period;
        private long nextExecution;
        private final Runnable task;

        SchedulerRecord(long period, long nextExecution, Runnable task) {
            this.period = period;
            this.nextExecution = nextExecution;
            this.task = task;
        }

        void run(long currentTime) {

        }
    }

    private static final class ScheduledTask implements Task, Cancellable, Comparable<ScheduledTask> {

        private final Task task;
        private final Queue<Task> queue;

        private final float period;
        private float atAge;
        private float age;

        private boolean cancelled;

        ScheduledTask(Task task, float atAge, float period, Queue<Task> queue) {
            this.task = task;
            this.queue = queue;
            this.period = period;
            this.atAge = atAge;
        }

        @Override
        public void run(float deltaTime) {
            if (!isCancelled()) {
                age += deltaTime;
                run0(deltaTime);
            }
        }

        private void run0(float deltaTime) {
            if (age >= atAge) {
                task.run(deltaTime);
                if (period > 0) {
                    atAge += period;
                    queue.add(this);
                }
            } else {
                queue.add(this);
            }
        }

        @Override
        public void cancel() {
            cancelled = true;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public int compareTo(ScheduledTask o) {
            if (this == o) {
                return 0;
            }
            return atAge > o.atAge ? -1 : atAge == o.atAge ? 0 : 1;
        }
    }

    public static interface Cancellable {

        void cancel();

        boolean isCancelled();
    }

    public static interface Task {

        void run(float deltaTime);
    }
}
