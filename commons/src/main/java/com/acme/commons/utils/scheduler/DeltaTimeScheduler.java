package com.acme.commons.utils.scheduler;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

public class DeltaTimeScheduler {

	private final Queue<DeferredTask<?>> taskQueue = new LinkedList<>();
	private final Queue<DeferredTask<?>> tasksToRun = new LinkedList<>();

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
}
