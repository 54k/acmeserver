package com.acme.server.position;

import com.acme.ecs.core.Node;
import com.acme.server.managers.WorldTransform;

public interface PositionNode extends Node {

    Transform getTransform();

    WorldTransform getWorldTransform();
}
