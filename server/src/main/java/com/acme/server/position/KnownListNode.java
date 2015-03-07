package com.acme.server.position;

import com.acme.engine.ecs.core.Node;

public interface KnownListNode extends Node {

    Transform getTransform();

    KnownList getKnownList();
}
