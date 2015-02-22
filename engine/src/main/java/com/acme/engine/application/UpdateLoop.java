package com.acme.engine.application;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UpdateLoop implements Context {

    private static final long MICROS_IN_SECOND = 1000000;
    private static final long ORIGIN_NANOS = System.nanoTime();

    private static final int STATE_STARTING = 0;
    private static final int STATE_STARTED = 1;
    private static final int STATE_DISPOSED = 2;

    private static final AtomicInteger counter = new AtomicInteger();

    private final AtomicInteger state = new AtomicInteger(STATE_STARTING);

    private final Queue<ScheduledTask> scheduledTasks = new PriorityQueue<>();
    private final Queue<ScheduledTask> taskQueue = new PriorityQueue<>();

    private final Application application;
    private String threadName;

    private final Lock lock = new ReentrantLock();
    private final Condition createdCondition = lock.newCondition();
    private final Condition disposedCondition = lock.newCondition();

    private final long updateIntervalNanos;
    private long nextUpdateNanos;

    private long lastNanos;
    private volatile float delta;
    private volatile Thread mainLoopThread;

    public UpdateLoop(Application application, int fps) {
        this(application, fps, createThreadName());
    }

    public UpdateLoop(Application application, int fps, String threadName) {
        this.application = application;
        updateIntervalNanos = 1000 / fps * MICROS_IN_SECOND;
        this.threadName = threadName;
    }

    private static String createThreadName() {
        return "aegis-loop-" + counter.incrementAndGet();
    }

    private void runLoop() {
        mainLoopThread = new Thread(threadName) {
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
            try {
                application.create(this);
                signalStarted();
            } catch (Throwable t) {
                application.handleError(t);
            }

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
        } finally {
            application.dispose();
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
    public float getDelta() {
        return delta;
    }

    @Override
    public CancellableTask schedule(Runnable task) {
        return schedule(task, 0, TimeUnit.NANOSECONDS);
    }

    @Override
    public CancellableTask schedule(Runnable task, long delay, TimeUnit unit) {
        return schedulePeriodic(task, delay, 0, unit);
    }

    @Override
    public CancellableTask schedulePeriodic(Runnable task, long delay, long period, TimeUnit unit) {
        if (isDisposed()) {
            throw new IllegalStateException("UpdateLoop has been disposed");
        }
        synchronized (scheduledTasks) {
            ScheduledTask scheduledTask = new ScheduledTask(task, unit.toNanos(delay), unit.toNanos(period), scheduledTasks);
            scheduledTasks.add(scheduledTask);
            return scheduledTask;
        }
    }

    @Override
    public void start() {
        runLoop();
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
    public void dispose() {
        if (Thread.currentThread() == mainLoopThread) {
            dispose0();
        } else {
            schedule(this::dispose0);
        }
    }

    public void dispose0() {
        if (!isDisposed()) {
            state.set(STATE_DISPOSED);
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

    private static class ScheduledTask implements Runnable, CancellableTask, Comparable<ScheduledTask> {

        final Runnable task;
        final Queue<ScheduledTask> queue;
        final long period;
        long nextExecutionNanos;
        volatile boolean cancelled;

        ScheduledTask(Runnable task, long delay, long period, Queue<ScheduledTask> queue) {
            this.task = task;
            this.queue = queue;
            this.period = period;
            nextExecutionNanos = nanos() + delay;
        }

        @Override
        public void run() {
            if (!isCancelled()) {
                run0();
            }
        }

        private void run0() {
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
}
