package com.acme.server.model.system;

import com.acme.commons.network.SessionComponent;
import com.acme.commons.utils.collections.NodeList;
import com.acme.ecs.core.Wire;
import com.acme.ecs.systems.NodeIteratingSystem;
import com.acme.server.model.event.KnownListListener;
import com.acme.server.model.node.KnownListNode;
import com.acme.server.model.node.TransformNode;
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
        NodeList<TransformNode> knownNodes = knownListNode.getKnownList().knownNodes;
        for (int i = knownNodes.size() - 1; i >= 0; i--) {
            TransformNode transformNode = knownNodes.get(i);
            knownNodes.remove(i);
            event(KnownListListener.class).dispatch().onKnownNodeRemoved(knownListNode, transformNode);
            sendDespawnPacket(knownListNode, transformNode);
        }
    }

    private void removeUnknownNodes(KnownListNode knownListNode) {
        NodeList<TransformNode> knownNodes = knownListNode.getKnownList().knownNodes;
        for (int i = knownNodes.size() - 1; i >= 0; i--) {
            TransformNode transformNode = knownNodes.get(i);
            if (isUnknownFor(knownListNode, transformNode)) {
                knownNodes.remove(i);
                event(KnownListListener.class).dispatch().onKnownNodeRemoved(knownListNode, transformNode);
                sendDespawnPacket(knownListNode, transformNode);
            }
        }
    }

    private boolean isUnknownFor(KnownListNode knownListNode, TransformNode transformNode) {
        int distanceToForget = knownListNode.getKnownList().distanceToForget;
        return !transformNode.getWorld().spawned ||
                PositionUtils.isOutOfRange(knownListNode.getEntity(), transformNode.getEntity(), distanceToForget);
    }

    private void sendDespawnPacket(KnownListNode knownListNode, TransformNode transformNode) {
        if (knownListNode.getEntity().hasComponent(SessionComponent.class)) {
            packetSystem.sendPacket(knownListNode.getEntity(), new DespawnPacket(transformNode.getEntity()));
        }
    }

    private void addKnownNodes(KnownListNode knownListNode) {
        NodeList<TransformNode> knownNodes = knownListNode.getKnownList().knownNodes;
        Region region = knownListNode.getWorld().region;
        if (region == null) {
            return;
        }

        for (Region r : region.getSurroundingRegions()) {
            Iterable<TransformNode> transformNodes = r.getEntities().transform(TransformNode.class);
            for (TransformNode transformNode : transformNodes) {
                if (knownListNode.equals(transformNode)) {
                    continue;
                }

                if (isKnownFor(knownListNode, transformNode)) {
                    knownNodes.add(transformNode);
                    event(KnownListListener.class).dispatch().onKnownNodeAdded(knownListNode, transformNode);
                    sendSpawnPacket(knownListNode, transformNode);
                }
            }
        }
    }

    private void sendSpawnPacket(KnownListNode knownListNode, TransformNode transformNode) {
        if (knownListNode.getEntity().hasComponent(SessionComponent.class)) {
            packetSystem.sendPacket(knownListNode.getEntity(), new SpawnPacket(transformNode.getEntity()));
        }
    }

    private boolean isKnownFor(KnownListNode knownListNode, TransformNode transformNode) {
        return transformNode.getWorld().spawned &&
                !isAlreadyKnownBy(knownListNode, transformNode) && isInRange(knownListNode, transformNode);
    }

    private boolean isAlreadyKnownBy(KnownListNode knownListNode, TransformNode transformNode) {
        return knownListNode.getKnownList().knownNodes.contains(transformNode);
    }

    private boolean isInRange(KnownListNode knownListNode, TransformNode visibleNode) {
        int distanceToFind = knownListNode.getKnownList().distanceToFind;
        return PositionUtils.isInRange(visibleNode.getEntity(), knownListNode.getEntity(), distanceToFind);
    }
}
