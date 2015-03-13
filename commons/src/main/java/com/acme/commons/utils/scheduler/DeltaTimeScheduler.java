package com.acme.commons.utils.scheduler;

import com.acme.ecs.utils.Pool;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

public class DeltaTimeScheduler implements Pool.Disposable {

	private final Queue<DeferredTask<?>> scheduledTasks = new LinkedList<>();
	private final Queue<DeferredTask<?>> tasksToRun = new LinkedList<>();

	@SuppressWarnings("unchecked")
	public void update(float deltaTime) {
		tasksToRun.addAll(scheduledTasks);
		scheduledTasks.clear();
		while (!tasksToRun.isEmpty()) {
			DeferredTask task = tasksToRun.poll();
			execute(task, deltaTime);
		}
	}

	private void execute(DeferredTask<? super Object> task, float deltaTime) {
		if (task.cancelled) {
			return;
		}

		task.age += deltaTime;
		if (task.age < task.atAge) {
			scheduledTasks.add(task);
			return;
		}

		try {
			Object result = task.task.call();
			if (task.period > -1) {
				task.atAge += task.period;
				scheduledTasks.add(task);
			} else {
				task.resolve(result);
			}
		} catch (Throwable t) {
			task.reject(t);
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
		DeferredTask<T> t = new DeferredTask<>(task, delay, -1);
		scheduledTasks.add(t);
		return t;
	}

	/**
	 * Submits the given task with the given delay and period.
	 *
	 * @param task   task
	 * @param delay  delay
	 * @param period period
	 */
	public PromiseTask<Void> schedule(Runnable task, float delay, float period) {
		DeferredTask<Void> t = new DeferredTask<>(Executors.callable(task, null), delay, period);
		scheduledTasks.add(t);
		return t;
	}

	/**
	 * Clears task queue, all remaining tasks will be cancelled
	 */
	public void clear() {
		while (!scheduledTasks.isEmpty()) {
			scheduledTasks.poll().cancel();
		}
	}

	@Override
	public void dispose() {
		clear();
	}
}
