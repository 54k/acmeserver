package com.acme.core.application;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

final class UpdateLoop implements Context {

    private static final long MICROS_IN_SECOND = 1000000;
    private static final long ORIGIN_NANOS = System.nanoTime();

    private final Set<ContextListener> contextListeners = new LinkedHashSet<>();

    private final Queue<ScheduledTask> scheduledTasks = new PriorityQueue<>();
    private final Queue<ScheduledTask> tasks = new PriorityQueue<>();

    private final Application application;
    private final Configuration configuration;
    private final Map<String, Object> injectables = new HashMap<>();

    private final Lock lock = new ReentrantLock();
    private volatile boolean initialized = false;
    private final Condition initializedCondition = lock.newCondition();
    private volatile boolean running = true;
    private final Condition disposedCondition = lock.newCondition();

    private final long updateIntervalNanos;
    private long nextUpdateNanos;

    private long lastNanos;
    private volatile float delta;

    UpdateLoop(Application application, Configuration configuration) {
        this.application = application;
        this.configuration = configuration;
        updateIntervalNanos = configuration.updateInterval * MICROS_IN_SECOND;
        contextListeners.addAll(configuration.contextListeners);
        runLoop();
    }

    private void runLoop() {
        Thread mainLoopThread = new Thread(configuration.applicationName) {
            @Override
            public void run() {
                try {
                    UpdateLoop.this.loop();
                } finally {
                    signalDisposed();
                }
            }
        };
        mainLoopThread.start();
    }

    private void signalDisposed() {
        if (!initialized) {
            signalInitialized();
        }

        running = false;
        lock.lock();
        try {
            disposedCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private void loop() {
        application.create(this);
        synchronized (contextListeners) {
            contextListeners.forEach(ContextListener::created);
        }
        signalInitialized();
        while (running) {
            if (updateIntervalNanos > 0) {
                waitForUpdate();
            }
            updateDelta();
            executeTasks();
            if (!running) {
                break;
            }
            try {
                application.update();
            } catch (Throwable t) {
                application.handleError(t);
            }
        }
        application.dispose();
        synchronized (contextListeners) {
            contextListeners.forEach(ContextListener::disposed);
        }
    }

    private void signalInitialized() {
        initialized = true;
        lock.lock();
        try {
            initializedCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private void waitForUpdate() {
        long currentNanos = nanos();
        if (nextUpdateNanos > currentNanos) {
            try {
                Thread.sleep((nextUpdateNanos - currentNanos) / MICROS_IN_SECOND);
            } catch (InterruptedException ignore) {
            }
            nextUpdateNanos = nanos() + updateIntervalNanos;
        } else {
            nextUpdateNanos = currentNanos + updateIntervalNanos;
        }
    }

    private void updateDelta() {
        long currentNanos = nanos();
        delta = (currentNanos - lastNanos) / MICROS_IN_SECOND;
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
            try {
                tasks.poll().run();
            } catch (Throwable t) {
                application.handleError(t);
            }
        }
    }

    @Override
    public void addContextListener(ContextListener contextListener) {
        synchronized (contextListeners) {
            contextListeners.add(contextListener);
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
            throw new IllegalStateException(configuration.applicationName + " disposed");
        }
        synchronized (scheduledTasks) {
            scheduledTasks.add(new ScheduledTask(task, unit.toNanos(delay), unit.toNanos(period), scheduledTasks));
        }
    }

    @Override
    public void dispose() {
        schedule(() -> running = false);
    }

    @Override
    public void waitForStart(long timeoutMillis) {
        lock.lock();
        try {
            while (!initialized) {
                try {
                    initializedCondition.await(timeoutMillis, TimeUnit.MILLISECONDS);
                } catch (InterruptedException ignore) {
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void waitForDispose(long timeoutMillis) {
        lock.lock();
        try {
            while (running) {
                try {
                    disposedCondition.await(timeoutMillis, TimeUnit.MILLISECONDS);
                } catch (InterruptedException ignore) {
                }
            }
        } finally {
            lock.unlock();
        }
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
        @SuppressWarnings("NullableProblems")
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
