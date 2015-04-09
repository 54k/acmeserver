package com.acme.server.model.node;

import com.acme.server.model.component.SpawnComponent;

public interface SpawnNode extends PositionNode {

    SpawnComponent getSpawn();
}
