package com.acme.server.position;

import com.acme.ecs.core.Node;
import com.acme.server.managers.WorldTransform;

public interface TransformNode extends Node {

    Transform getTransform();

    WorldTransform getWorldTransform();
}
