package com.acme.server.model.event;

import com.acme.ecs.events.EventListener;
import com.acme.server.model.node.KnownListNode;
import com.acme.server.model.node.TransformNode;

public interface KnownListListener extends EventListener {

    void onKnownNodeAdded(KnownListNode knownListNode, TransformNode transformNode);

    void onKnownNodeRemoved(KnownListNode knownListNode, TransformNode transformNode);
}
