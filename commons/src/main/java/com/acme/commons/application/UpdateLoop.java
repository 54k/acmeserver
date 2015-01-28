package com.acme.commons.application;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

final class UpdateLoop implements Context {

    private static final long NANOS_IN_SECOND = 1000000;
    private static final long ORIGIN_NANOS = System.nanoTime();

    private final Queue<ScheduledTask> scheduledTasks = new PriorityQueue<>();
    private final Queue<ScheduledTask> tasks = new PriorityQueue<>();

    private final Application application;
    private final Configuration configuration;
    private final Map<String, Object> injectables = new HashMap<>();

    private volatile boolean running = true;

    private final long updateIntervalNanos;
    private long nextUpdateNanos;

    private long lastNanos;
    private volatile float delta;

    UpdateLoop(Application application, Configuration configuration) {
        this.application = application;
        this.configuration = configuration;
        updateIntervalNanos = configuration.getUpdateInterval() * NANOS_IN_SECOND;
        runLoop();
    }

    private void runLoop() {
        Thread mainLoopThread = new Thread(configuration.getApplicationName()) {
            @Override
            public void run() {
                UpdateLoop.this.loop();
            }
        };
        mainLoopThread.start();
    }

    private void loop() {
        application.create(this);
        while (running) {
            if (updateIntervalNanos > 0) {
                waitForUpdate();
            }
            updateDelta();
            try {
                executeTasks();
                if (!running) {
                    break;
                }
                application.update();
            } catch (Throwable t) {
                application.handleError(t);
            }
        }
        application.dispose();
    }

    private void waitForUpdate() {
        long currentNanos = nanos();
        if (nextUpdateNanos > currentNanos) {
            try {
                Thread.sleep((nextUpdateNanos - currentNanos) / NANOS_IN_SECOND);
            } catch (InterruptedException ignore) {
            }
            nextUpdateNanos = nanos() + updateIntervalNanos;
        } else {
            nextUpdateNanos = currentNanos + updateIntervalNanos;
        }
    }

    private void updateDelta() {
        long currentNanos = nanos();
        delta = (currentNanos - lastNanos) / NANOS_IN_SECOND;
        lastNanos = currentNanos;
    }

    private static long nanos() {
        return System.nanoTime() - ORIGIN_NANOS;
    }

    private void executeTasks() {
        synchronized (scheduledTasks) {
            tasks.addAll(scheduledTasks);
            scheduledTasks.clear();
        }
        while (!tasks.isEmpty()) {
            tasks.poll().run();
        }
    }

    @Override
    public <T> void register(Class<T> clazz, T object) {
        register(clazz, "", object);
    }

    @Override
    public <T> void register(Class<T> clazz, String name, T object) {
        injectables.put(nameFor(clazz, name), object);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Class<T> clazz) {
        return get(clazz, "");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Class<T> clazz, String name) {
        return (T) injectables.get(nameFor(clazz, name));
    }

    private static <T> String nameFor(Class<T> clazz, String name) {
        return clazz.getName() + "$" + name;
    }

    @Override
    public float getDelta() {
        return delta;
    }

    @Override
    public void schedule(Runnable task) {
        schedule(task, 0, TimeUnit.NANOSECONDS);
    }

    @Override
    public void schedule(Runnable task, long delay, TimeUnit unit) {
        schedulePeriodic(task, delay, 0, unit);
    }

    @Override
    public void schedulePeriodic(Runnable task, long delay, long period, TimeUnit unit) {
        if (!running) {
            throw new IllegalStateException(configuration.getApplicationName() + " disposed");
        }
        scheduledTasks.add(new ScheduledTask(task, unit.toNanos(delay), unit.toNanos(period), scheduledTasks));
    }

    @Override
    public void dispose() {
        schedule(() -> running = false);
    }

    private static final class ScheduledTask implements Runnable, Comparable<ScheduledTask> {

        final Runnable task;
        final Queue<ScheduledTask> queue;
        final long period;
        long nextExecutionNanos;

        ScheduledTask(Runnable task, long delay, long period, Queue<ScheduledTask> queue) {
            this.task = task;
            this.queue = queue;
            this.period = period;
            nextExecutionNanos = nanos() + delay;
        }

        @Override
        public void run() {
            long currentNanos = nanos();
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
        public int compareTo(ScheduledTask o) {
            if (nextExecutionNanos > o.nextExecutionNanos) {
                return 1;
            } else if (nextExecutionNanos == o.nextExecutionNanos) {
                return 0;
            }
            return -1;
        }
    }
}
