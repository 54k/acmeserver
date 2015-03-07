package com.acme.server.position;

import com.acme.engine.ecs.core.Node;
import com.acme.server.managers.WorldComponent;

public interface WorldNode extends Node {

    WorldComponent getWorld();

    Spawn getSpawn();

    Transform getTransform();
}
