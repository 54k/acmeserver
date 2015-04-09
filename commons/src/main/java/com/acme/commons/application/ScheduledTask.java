package com.acme.commons.application;

import java.util.Queue;

final class ScheduledTask implements Runnable, CancellableTask, Comparable<ScheduledTask> {

    final Runnable task;
    final Queue<ScheduledTask> queue;
    final long period;
    long nextExecutionNanos;
    volatile boolean cancelled;

    ScheduledTask(Runnable task, long delay, long period, Queue<ScheduledTask> queue) {
        this.task = task;
        this.queue = queue;
        this.period = period;
        nextExecutionNanos = UpdateLoop.nanos() + delay;
    }

    @Override
    public void run() {
        if (!isCancelled()) {
            run0();
        }
    }

    private void run0() {
        long currentNanos = UpdateLoop.nanos();
        if (nextExecutionNanos <= currentNanos) {
            task.run();
            if (period > 0) {
                nextExecutionNanos += period;
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
        if (o == null) {
            throw new IllegalArgumentException();
        }
        return nextExecutionNanos > o.nextExecutionNanos ? 1 : nextExecutionNanos == o.nextExecutionNanos ? 0 : -1;
    }
}
