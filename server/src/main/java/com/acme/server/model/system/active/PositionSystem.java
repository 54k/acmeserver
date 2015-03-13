package com.acme.server.model.system.active;

import com.acme.commons.timer.SchedulerSystem;
import com.acme.commons.utils.promise.Deferred;
import com.acme.commons.utils.promise.Promise;
import com.acme.commons.utils.scheduler.PromiseTask;
import com.acme.ecs.core.Engine;
import com.acme.ecs.core.Entity;
import com.acme.ecs.core.Node;
import com.acme.ecs.core.NodeListener;
import com.acme.ecs.core.Wire;
import com.acme.ecs.systems.NodeIteratingSystem;
import com.acme.ecs.systems.PassiveSystem;
import com.acme.server.model.component.PositionComponent;
import com.acme.server.model.component.WorldComponent;
import com.acme.server.model.event.PositionListener;
import com.acme.server.model.node.PositionNode;
import com.acme.server.packets.PacketSystem;
import com.acme.server.packets.outbound.MovePacket;
import com.acme.server.world.Position;
import com.acme.server.world.Region;

import java.util.HashMap;
import java.util.Map;

public class PositionSystem extends PassiveSystem implements NodeListener {

	@Wire
	private SchedulerSystem schedulerSystem;
	@Wire
	private PacketSystem packetSystem;

	private final Map<PositionNode, MoveTask> scheduledMoves = new HashMap<>();

	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		engine.addNodeListener(PositionNode.class, this);
	}

	@Override
	public void nodeAdded(Node node) {
	}

	@Override
	public void nodeRemoved(Node node) {
		stopMove((PositionNode) node);
	}

	/**
	 * Spawns the given node
	 *
	 * @param node node
	 */
	public void spawn(PositionNode node) {
		PositionComponent position = node.getPosition();
		if (!position.spawned) {
			position.spawned = true;
			position.region = node.getWorld().instance.findRegion(position.position);
			position.region.removeEntity(node.getEntity());
			updateRegionMembership(node);
			event(PositionListener.class).dispatch().onNodeSpawned(node);
		}
	}

	/**
	 * Decays the given node
	 *
	 * @param node node
	 */
	public void decay(PositionNode node) {
		PositionComponent position = node.getPosition();
		if (position.spawned) {
			stopMove(node);
			position.spawned = false;
			if (position.region != null) {
				position.region.removeEntity(node.getEntity());
			}
			event(PositionListener.class).dispatch().onNodeDecayed(node);
		}
	}

	/**
	 * Submits move task for the given node
	 *
	 * @param node     node
	 * @param position new position
	 */
	public Promise<PositionNode, PositionNode> moveTo(PositionNode node, Position position) {
		stopMove(node);
		return submitMoveTask(node, position);
	}

	/**
	 * Cancels move task for the given node
	 *
	 * @param node node
	 */
	public void stopMove(PositionNode node) {
		MoveTask moveTask = scheduledMoves.remove(node);
		if (moveTask != null) {
			moveTask.cancel();
		}
	}

	private Promise<PositionNode, PositionNode> submitMoveTask(PositionNode node, Position position) {
		MoveTask moveTask = new MoveTask(node, position);
		moveTask.schedule();
		scheduledMoves.put(node, moveTask);
		return moveTask.promise;
	}

	/**
	 * Updates position and region membership of the given node.
	 * Current move task of the given node will be cancelled.
	 *
	 * @param node     node
	 * @param position new position
	 */
	public void teleportTo(PositionNode node, Position position) {
		stopMove(node);
		setPosition(node, position);
	}

	private void setPosition(PositionNode node, Position position) {
		PositionComponent transform = node.getPosition();
		transform.position.setPosition(position);
		updateRegionMembership(node);
	}

	private void updateRegionMembership(PositionNode node) {
		PositionComponent position = node.getPosition();
		WorldComponent world = node.getWorld();

		Region oldRegion = position.region;
		Region newRegion = world.instance.findRegion(position.position);

		if (oldRegion != newRegion) {
			newRegion.addEntity(node.getEntity());
			position.region = newRegion;
		}
	}

	private class MoveTask implements Runnable {

		final PositionNode node;
		final Position destination;
		PromiseTask<Void> scheduledTask;
		Deferred<PositionNode, PositionNode> promise;

		MoveTask(PositionNode node, Position destination) {
			this.node = node;
			this.destination = destination;
		}

		void schedule() {
			promise = new Deferred<>();
			scheduledTask = schedulerSystem.schedule(this, 0, 0);
		}

		void cancel() {
			if (promise.isPending()) {
				promise.reject(node);
			}
			if (scheduledTask.isPending()) {
				scheduledTask.cancel();
			}
		}

		@Override
		public void run() {
			Entity entity = node.getEntity();
			packetSystem.sendToSelfAndRegion(entity, new MovePacket(entity));
			setPosition(node, destination);
			promise.resolve(node);
			stopMove(node);
		}
	}
}
