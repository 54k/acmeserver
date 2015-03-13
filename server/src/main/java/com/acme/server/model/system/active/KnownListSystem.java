package com.acme.server.model.system.active;

import com.acme.commons.network.SessionComponent;
import com.acme.commons.collections.NodeList;
import com.acme.ecs.core.Wire;
import com.acme.ecs.systems.NodeIteratingSystem;
import com.acme.server.model.event.KnownListListener;
import com.acme.server.model.node.KnownListNode;
import com.acme.server.model.node.PositionNode;
import com.acme.server.packets.PacketSystem;
import com.acme.server.packets.outbound.DespawnPacket;
import com.acme.server.packets.outbound.SpawnPacket;
import com.acme.server.utils.PositionUtils;
import com.acme.server.world.Region;

public class KnownListSystem extends NodeIteratingSystem<KnownListNode> {

    @Wire
    private PacketSystem packetSystem;

    public KnownListSystem() {
        super(KnownListNode.class);
    }

    @Override
    protected void processNode(KnownListNode knownListNode, float deltaTime) {
        updateKnownList(knownListNode);
    }

    /**
     * Updates known list for the given node
     *
     * @param knownListNode node
     */
    public void updateKnownList(KnownListNode knownListNode) {
        removeUnknownNodes(knownListNode);
        addKnownNodes(knownListNode);
    }

    /**
     * Clears known list for the given node
     *
     * @param knownListNode node
     */
    public void clearKnownList(KnownListNode knownListNode) {
        NodeList<PositionNode> knownNodes = knownListNode.getKnownList().knownNodes;
        for (int i = knownNodes.size() - 1; i >= 0; i--) {
            PositionNode positionNode = knownNodes.get(i);
            knownNodes.remove(i);
            event(KnownListListener.class).dispatch().onKnownNodeRemoved(knownListNode, positionNode);
            sendDespawnPacket(knownListNode, positionNode);
        }
    }

    private void removeUnknownNodes(KnownListNode knownListNode) {
        NodeList<PositionNode> knownNodes = knownListNode.getKnownList().knownNodes;
        for (int i = knownNodes.size() - 1; i >= 0; i--) {
            PositionNode positionNode = knownNodes.get(i);
            if (isUnknownFor(knownListNode, positionNode)) {
                knownNodes.remove(i);
                event(KnownListListener.class).dispatch().onKnownNodeRemoved(knownListNode, positionNode);
                sendDespawnPacket(knownListNode, positionNode);
            }
        }
    }

    private boolean isUnknownFor(KnownListNode knownListNode, PositionNode positionNode) {
        int distanceToForget = knownListNode.getKnownList().distanceToForget;
        return !positionNode.getPosition().spawned ||
                PositionUtils.isOutOfRange(knownListNode.getEntity(), positionNode.getEntity(), distanceToForget);
    }

    private void sendDespawnPacket(KnownListNode knownListNode, PositionNode positionNode) {
        if (knownListNode.getEntity().hasComponent(SessionComponent.class)) {
            packetSystem.sendPacket(knownListNode.getEntity(), new DespawnPacket(positionNode.getEntity()));
        }
    }

    private void addKnownNodes(KnownListNode knownListNode) {
        NodeList<PositionNode> knownNodes = knownListNode.getKnownList().knownNodes;
        Region region = knownListNode.getPosition().region;
        if (region == null) {
            return;
        }

        for (Region r : region.getSurroundingRegions()) {
            Iterable<PositionNode> transformNodes = r.getEntities().transform(PositionNode.class);
            for (PositionNode positionNode : transformNodes) {
                if (knownListNode.equals(positionNode)) {
                    continue;
                }

                if (isKnownFor(knownListNode, positionNode)) {
                    knownNodes.add(positionNode);
                    event(KnownListListener.class).dispatch().onKnownNodeAdded(knownListNode, positionNode);
                    sendSpawnPacket(knownListNode, positionNode);
                }
            }
        }
    }

    private void sendSpawnPacket(KnownListNode knownListNode, PositionNode positionNode) {
        if (knownListNode.getEntity().hasComponent(SessionComponent.class)) {
            packetSystem.sendPacket(knownListNode.getEntity(), new SpawnPacket(positionNode.getEntity()));
        }
    }

    private boolean isKnownFor(KnownListNode knownListNode, PositionNode positionNode) {
        return positionNode.getPosition().spawned &&
                !isAlreadyKnownBy(knownListNode, positionNode) && isInRange(knownListNode, positionNode);
    }

    private boolean isAlreadyKnownBy(KnownListNode knownListNode, PositionNode positionNode) {
        return knownListNode.getKnownList().knownNodes.contains(positionNode);
    }

    private boolean isInRange(KnownListNode knownListNode, PositionNode visibleNode) {
        int distanceToFind = knownListNode.getKnownList().distanceToFind;
        return PositionUtils.isInRange(visibleNode.getEntity(), knownListNode.getEntity(), distanceToFind);
    }
}
