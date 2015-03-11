package com.acme.server.model.event;

import com.acme.ecs.events.EventListener;
import com.acme.server.model.node.WorldNode;

public interface WorldListener extends EventListener {

    void onWorldNodeAdded(WorldNode worldNode);

    void onWorldNodeRemoved(WorldNode worldNode);

    void onWorldNodeSpawned(WorldNode worldNode);

    void onWorldNodeDecayed(WorldNode worldNode);
}
