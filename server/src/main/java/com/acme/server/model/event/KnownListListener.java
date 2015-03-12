package com.acme.server.model.event;

import com.acme.ecs.events.EventListener;
import com.acme.server.model.node.KnownListNode;
import com.acme.server.model.node.PositionNode;

public interface KnownListListener extends EventListener {

    void onKnownNodeAdded(KnownListNode knownListNode, PositionNode positionNode);

    void onKnownNodeRemoved(KnownListNode knownListNode, PositionNode positionNode);
}
