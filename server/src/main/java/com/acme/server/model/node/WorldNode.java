package com.acme.server.model.node;

import com.acme.ecs.core.Node;
import com.acme.server.model.component.WorldComponent;

public interface WorldNode extends Node {

    WorldComponent getWorld();
}
