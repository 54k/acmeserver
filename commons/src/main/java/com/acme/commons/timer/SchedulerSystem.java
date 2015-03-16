package com.acme.commons.timer;

import com.acme.commons.utils.scheduler.PromiseTask;
import com.acme.commons.utils.scheduler.DeltaTimeScheduler;
import com.acme.ecs.core.Engine;
import com.acme.ecs.core.Entity;
import com.acme.ecs.core.EntityListener;
import com.acme.ecs.core.EntitySystem;
import com.acme.ecs.utils.Pool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class SchedulerSystem extends EntitySystem implements EntityListener {

	private final DeltaTimeScheduler scheduler;
	private final Map<Entity, DeltaTimeScheduler> entitySchedulers;
	private final SchedulerPool schedulerPool;

	public SchedulerSystem() {
		this(0);
	}

	public SchedulerSystem(int priority) {
		super(priority);
		scheduler = new DeltaTimeScheduler();
		entitySchedulers = new HashMap<>();
		schedulerPool = new SchedulerPool();
	}

	@Override
	public void addedToEngine(Engine engine) {
		engine.addEntityListener(this);
	}

	@Override
	public void entityAdded(Entity entity) {
		if (entitySchedulers.get(entity) == null) {
			entitySchedulers.put(entity, schedulerPool.newObject());
		}
	}

	@Override
	public void entityRemoved(Entity entity) {
		DeltaTimeScheduler s = entitySchedulers.remove(entity);
		if (s != null) {
			schedulerPool.free(s);
		}
	}

	@Override
	public void update(float deltaTime) {
		scheduler.update(deltaTime);
		for (DeltaTimeScheduler s : entitySchedulers.values()) {
			s.update(deltaTime);
		}
	}

	/**
	 * Submits the given task with no delay.
	 *
	 * @param task task
	 */
	public PromiseTask<Void> schedule(Runnable task) {
		return scheduler.schedule(task);
	}

	/**
	 * Submits the given task with no delay.
	 *
	 * @param task task
	 */
	public <T> PromiseTask<T> schedule(Callable<T> task) {
		return scheduler.schedule(task);
	}

	/**
	 * Submits the given task with the given delay.
	 *
	 * @param task  task
	 * @param delay delay in milliseconds
	 */
	public PromiseTask<Void> schedule(Runnable task, float delay) {
		return scheduler.schedule(task, delay);
	}

	/**
	 * Submits the given task with the given delay.
	 *
	 * @param task  task
	 * @param delay delay in milliseconds
	 */
	public <T> PromiseTask<T> schedule(Callable<T> task, float delay) {
		return scheduler.schedule(task, delay);
	}

	/**
	 * Submits the given task with the given delay and period.
	 *
	 * @param task   task
	 * @param delay  delay in milliseconds
	 * @param period period in milliseconds
	 */
	public PromiseTask<Void> schedule(Runnable task, float delay, float period) {
		return scheduler.schedule(task, delay, period);
	}

	/**
	 * Submits the given task with no delay for the given entity.
	 * If entity will be removed from engine, then all submitted tasks will be cancelled.
	 *
	 * @param entity entity
	 * @param task   task
	 */
	public PromiseTask<Void> scheduleForEntity(Entity entity, Runnable task) {
		return getSchedulerFor(entity).schedule(task);
	}

	/**
	 * Submits the given task with no delay for the given entity.
	 * If entity will be removed from engine, then all submitted tasks will be cancelled.
	 *
	 * @param entity entity
	 * @param task   task
	 */
	public <T> PromiseTask<T> scheduleForEntity(Entity entity, Callable<T> task) {
		return getSchedulerFor(entity).schedule(task);
	}

	/**
	 * Submits the given task with the given delay for the given entity.
	 * If entity will be removed from engine, then all submitted tasks will be cancelled.
	 *
	 * @param entity entity
	 * @param task   task
	 * @param delay  delay in milliseconds
	 */
	public PromiseTask<Void> scheduleForEntity(Entity entity, Runnable task, float delay) {
		return getSchedulerFor(entity).schedule(task, delay);
	}

	/**
	 * Submits the given task with the given delay for the given entity.
	 * If entity will be removed from engine, then all submitted tasks will be cancelled.
	 *
	 * @param entity entity
	 * @param task   task
	 * @param delay  delay in milliseconds
	 */
	public <T> PromiseTask<T> scheduleForEntity(Entity entity, Callable<T> task, float delay) {
		return getSchedulerFor(entity).schedule(task, delay);
	}

	/**
	 * Submits the given task with the given delay and period.
	 * If entity will be removed from engine, then all submitted tasks will be cancelled.
	 *
	 * @param entity entity
	 * @param task   task
	 * @param delay  delay in milliseconds
	 * @param period period in milliseconds
	 */
	public PromiseTask<Void> scheduleForEntity(Entity entity, Runnable task, float delay, float period) {
		return getSchedulerFor(entity).schedule(task, delay, period);
	}

	private DeltaTimeScheduler getSchedulerFor(Entity entity) {
		DeltaTimeScheduler scheduler = entitySchedulers.get(entity);
		if (scheduler == null) {
			scheduler = schedulerPool.newObject();
			entitySchedulers.put(entity, scheduler);
		}
		return scheduler;
	}

	private static final class SchedulerPool extends Pool<DeltaTimeScheduler> {
		@Override
		protected DeltaTimeScheduler newObject() {
			return new DisposableScheduler();
		}
	}

	private static final class DisposableScheduler extends DeltaTimeScheduler implements Pool.Disposable {
		@Override
		public void dispose() {
			clear();
		}
	}
}
