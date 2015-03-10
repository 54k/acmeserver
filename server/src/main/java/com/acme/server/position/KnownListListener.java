package com.acme.server.position;

import com.acme.ecs.events.EventListener;

public interface KnownListListener extends EventListener {

    void entityAdded(KnownListNode knownListNode, VisibleNode visibleNode);

    /**
     * Note that visibleNode's owner entity, could not contain required components.
     */
    void entityRemoved(KnownListNode knownListNode, VisibleNode visibleNode);
}
