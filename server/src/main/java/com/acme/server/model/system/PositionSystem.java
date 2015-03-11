package com.acme.server.model.system;

import com.acme.commons.timer.SchedulerSystem;
import com.acme.commons.utils.promises.Deferred;
import com.acme.commons.utils.promises.Promise;
import com.acme.commons.utils.scheduler.PromiseTask;
import com.acme.ecs.core.*;
import com.acme.ecs.systems.PassiveSystem;
import com.acme.server.model.component.TransformComponent;
import com.acme.server.model.component.WorldComponent;
import com.acme.server.model.node.TransformNode;
import com.acme.server.packets.PacketSystem;
import com.acme.server.packets.outbound.MovePacket;
import com.acme.server.world.Position;
import com.acme.server.world.Region;

import java.util.HashMap;
import java.util.Map;

public class PositionSystem extends PassiveSystem implements NodeListener {

    @Wire
    private PacketSystem packetSystem;
    @Wire
    private SchedulerSystem schedulerSystem;

    private final Map<TransformNode, MoveTask> scheduledMoves = new HashMap<>();

    @Override
    public void addedToEngine(Engine engine) {
        engine.addNodeListener(TransformNode.class, this);
    }

    @Override
    public void nodeAdded(Node node) {
    }

    @Override
    public void nodeRemoved(Node node) {
        stopMove((TransformNode) node);
    }

    /**
     * Submits move task for the given node
     *
     * @param node     node
     * @param position new position
     */
    public Promise<TransformNode, TransformNode> moveTo(TransformNode node, Position position) {
        stopMove(node);
        return submitMoveTask(node, position);
    }

    /**
     * Cancels move task for the given node
     *
     * @param node node
     */
    public void stopMove(TransformNode node) {
        MoveTask moveTask = scheduledMoves.remove(node);
        if (moveTask != null) {
            moveTask.cancel();
        }
    }

    private Promise<TransformNode, TransformNode> submitMoveTask(TransformNode node, Position position) {
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
    public void teleportTo(TransformNode node, Position position) {
        stopMove(node);
        setPosition(node, position);
    }

    private void setPosition(TransformNode node, Position position) {
        TransformComponent transform = node.getTransform();
        transform.position.setPosition(position);
        updateRegionMembership(node);
    }

    private void updateRegionMembership(TransformNode node) {
        TransformComponent transform = node.getTransform();
        WorldComponent worldTransform = node.getWorld();

        Region oldRegion = worldTransform.region;
        Region newRegion = worldTransform.instance.findRegion(transform.position);

        if (oldRegion != newRegion) {
            oldRegion.removeEntity(node.getEntity());
            newRegion.addEntity(node.getEntity());
            worldTransform.region = newRegion;
        }
    }

    private class MoveTask implements Runnable {

        final TransformNode node;
        final Position destination;
        PromiseTask<Void> scheduledTask;
        Deferred<TransformNode, TransformNode> promise;

        MoveTask(TransformNode node, Position destination) {
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
