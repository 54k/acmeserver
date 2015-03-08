package com.acme.server.position;

import com.acme.engine.ecs.core.*;
import com.acme.engine.ecs.systems.PassiveSystem;
import com.acme.engine.mechanics.timer.PromiseTask;
import com.acme.engine.mechanics.timer.SchedulerSystem;
import com.acme.server.managers.WorldComponent;
import com.acme.server.packets.PacketSystem;
import com.acme.server.packets.outbound.MovePacket;
import com.acme.server.world.Position;
import com.acme.server.world.Region;

import java.util.HashMap;
import java.util.Map;

@Wire
public class MovementSystem extends PassiveSystem implements NodeListener {

    private PacketSystem packetSystem;
    private SchedulerSystem schedulerSystem;

    private final Map<WorldNode, PromiseTask<Void>> scheduledMoves = new HashMap<>();

    @Override
    public void addedToEngine(Engine engine) {
        engine.addNodeListener(WorldNode.class, this);
    }

    @Override
    public void nodeAdded(Node node) {
    }

    @Override
    public void nodeRemoved(Node node) {
        stopMove((WorldNode) node);
    }

    @Deprecated
    public Position getPosition(Entity entity) {
        return entity.getComponent(Transform.class).getPosition();
    }

    /**
     * Submits move task for the specified node
     *
     * @param node     node
     * @param position new position
     */
    public PromiseTask<Void> moveTo(WorldNode node, Position position) {
        stopMove(node);
        return submitMoveTask(node, position);
    }

    /**
     * Cancels move task for the specified node
     *
     * @param node node
     */
    public void stopMove(WorldNode node) {
        PromiseTask<Void> moveTask = scheduledMoves.remove(node);
        if (moveTask != null) {
            moveTask.cancel();
        }
    }

    private PromiseTask<Void> submitMoveTask(WorldNode node, Position position) {
        MoveTask moveTask = new MoveTask(node, position);
        PromiseTask<Void> scheduledTask = schedulerSystem.schedule(moveTask);
        scheduledMoves.put(node, scheduledTask);
        return scheduledTask;
    }

    /**
     * Updates position and region membership of the specified node
     *
     * @param node     node
     * @param position new position
     */
    public void setPosition(WorldNode node, Position position) {
        Transform transform = node.getTransform();
        transform.setPosition(position);
        updateRegionMembership(node);
    }

    private void updateRegionMembership(WorldNode node) {
        Transform transform = node.getTransform();
        WorldComponent world = node.getWorld();

        Region oldRegion = transform.getRegion();
        Region newRegion = world.getInstance().findRegion(transform.getPosition());

        if (oldRegion != newRegion) {
            oldRegion.removeEntity(node.getEntity());
            newRegion.addEntity(node.getEntity());
            transform.setRegion(newRegion);
        }
    }

    private class MoveTask implements Runnable {
        private final WorldNode node;
        private final Position destination;

        public MoveTask(WorldNode node, Position destination) {
            this.node = node;
            this.destination = destination;
        }

        @Override
        public void run() {
            Entity entity = node.getEntity();
            packetSystem.sendToSelfAndRegion(entity, new MovePacket(entity));
            setPosition(node, destination);
            stopMove(node);
        }
    }
}
