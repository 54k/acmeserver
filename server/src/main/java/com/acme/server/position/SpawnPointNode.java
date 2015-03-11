package com.acme.server.position;

import com.acme.server.model.node.TransformNode;

public interface SpawnPointNode extends TransformNode {

    SpawnPoint getSpawn();
}
