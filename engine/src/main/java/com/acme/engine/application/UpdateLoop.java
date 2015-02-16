package com.acme.engine.application;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

final class UpdateLoop implements Context {

    private static final long MICROS_IN_SECOND = 1000000;
    private static final long ORIGIN_NANOS = System.nanoTime();

    private static final int STATE_STARTING = 0;
    private static final int STATE_STARTED = 1;
    private static final int STATE_DISPOSED = 2;

    private final AtomicInteger state = new AtomicInteger(STATE_STARTING);

    private final Queue<ContextListener> contextListeners = new ConcurrentLinkedQueue<>();

    private final Queue<ScheduledTask> scheduledTasks = new PriorityQueue<>();
    private final Queue<ScheduledTask> taskQueue = new PriorityQueue<>();

    private final Application application;
    private final Configuration configuration;
    private final Map<String, Object> injectables = new HashMap<>();

    private final Lock lock = new ReentrantLock();
    private final Condition createdCondition = lock.newCondition();
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
        if (!isStarted()) {
            signalStarted();
        }
        state.set(STATE_DISPOSED);
        lock.lock();
        try {
            disposedCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private boolean isStarted() {
        return state.get() >= STATE_STARTED;
    }

    private boolean isDisposed() {
        return state.get() >= STATE_DISPOSED;
    }

    private void loop() {
        try {
            application.create(this);
            signalStarted();
            contextListeners.forEach(ContextListener::created);

            while (!isDisposed()) {
                if (updateIntervalNanos > 0) {
                    waitForUpdate();
                }
                updateDelta();
                executeTasks();
                if (isDisposed()) {
                    break;
                }
                try {
                    application.update();
                } catch (Throwable t) {
                    application.handleError(t);
                }
            }
        } catch (Throwable t) {
            application.handleError(t);
        } finally {
            application.dispose();
            contextListeners.forEach(ContextListener::disposed);
        }
    }

    private void signalStarted() {
        state.set(STATE_STARTED);
        lock.lock();
        try {
            createdCondition.signalAll();
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
            taskQueue.addAll(scheduledTasks);
            scheduledTasks.clear();
        }
        while (!taskQueue.isEmpty()) {
            try {
                taskQueue.poll().run();
            } catch (Throwable t) {
                application.handleError(t);
            }
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
        if (isDisposed()) {
            throw new IllegalStateException(configuration.applicationName + " disposed");
        }
        synchronized (scheduledTasks) {
            scheduledTasks.add(new ScheduledTask(task, unit.toNanos(delay), unit.toNanos(period), scheduledTasks));
        }
    }

    @Override
    public void dispose() {
        schedule(() -> {
            if (!isDisposed()) {
                state.set(STATE_DISPOSED);
            }
        });
    }

    @Override
    public void waitForStart(long timeoutMillis) {
        lock.lock();
        try {
            while (!isStarted()) {
                try {
                    createdCondition.await(timeoutMillis, TimeUnit.MILLISECONDS);
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
            while (!isDisposed()) {
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
        public int compareTo(ScheduledTask o) {
            if (o == null) {
                throw new IllegalArgumentException();
            }
            return nextExecutionNanos > o.nextExecutionNanos ? 1 : nextExecutionNanos == o.nextExecutionNanos ? 0 : -1;
        }
    }
}
