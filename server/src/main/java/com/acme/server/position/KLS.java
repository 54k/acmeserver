package com.acme.server.position;

import com.acme.commons.utils.collections.NodeList;
import com.acme.commons.utils.collections.Predicates;
import com.acme.ecs.systems.NodeIteratingSystem;
import com.acme.server.utils.PositionUtils;
import com.acme.server.world.Region;

public class KLS extends NodeIteratingSystem<KnownListNode> {

    public KLS() {
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
        removeUnknownEntities(knownListNode);
        addKnownEntities(knownListNode);
    }

    private void removeUnknownEntities(KnownListNode knownListNode) {
        NodeList<VisibleNode> visibleNodes = knownListNode.getKnownList().visibleNodes;
        // in case of removed visible component
        NodeList<VisibleNode> notVisible = visibleNodes.query(Predicates.not(Predicates.node(VisibleNode.class)));
        for (VisibleNode node : notVisible) {
            visibleNodes.remove(node);
            event(KnownListListener.class).dispatch().entityRemoved(knownListNode, node);
        }

        for (VisibleNode node : visibleNodes) {
            if (isUnknownFor(knownListNode, node)) {
                visibleNodes.remove(node);
                event(KnownListListener.class).dispatch().entityRemoved(knownListNode, node);
            }
        }
    }

    private boolean isUnknownFor(KnownListNode knownListNode, VisibleNode visibleNode) {
        boolean alreadyKnownBy = isAlreadyKnownBy(knownListNode, visibleNode);
        return false;
    }

    private boolean isOutOfRange(KnownListNode knownListNode, VisibleNode visibleNode) {
        int distanceToForget = knownListNode.getKnownList().distanceToForget;
        return PositionUtils.isOutOfRange(visibleNode.getEntity(), knownListNode.getEntity(), distanceToForget);
    }

    private void addKnownEntities(KnownListNode knownListNode) {
        NodeList<VisibleNode> entities = knownListNode.getKnownList().visibleNodes;
        Region region = knownListNode.getTransform().region;
        for (Region r : region.getSurroundingRegions()) {
            Iterable<VisibleNode> visibleNodes = r.getEntities().query(Predicates.node(VisibleNode.class)).transform(VisibleNode.class);
            for (VisibleNode visibleNode : visibleNodes) {
                if (knownListNode.equals(visibleNode)) {
                    continue;
                }

                if (isKnownFor(knownListNode, visibleNode)) {
                    entities.add(visibleNode);
                    event(KnownListListener.class).dispatch().entityAdded(knownListNode, visibleNode);
                }
            }
        }
    }

    private boolean isKnownFor(KnownListNode knownListNode, VisibleNode visibleNode) {
        boolean alreadyKnown = isAlreadyKnownBy(knownListNode, visibleNode);
        return !alreadyKnown && isInRange(knownListNode, visibleNode);
    }

    private boolean isInRange(KnownListNode knownListNode, VisibleNode visibleNode) {
        int distanceToFind = knownListNode.getKnownList().distanceToFind;
        return PositionUtils.isInRange(visibleNode.getEntity(), knownListNode.getEntity(), distanceToFind);
    }

    private boolean isAlreadyKnownBy(KnownListNode knownListNode, VisibleNode visibleNode) {
        return knownListNode.equals(visibleNode) || knownListNode.getKnownList().visibleNodes.contains(visibleNode.getEntity());
    }
}
