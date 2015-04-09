package com.acme.server.model.event;

import com.acme.ecs.events.EventListener;
import com.acme.server.model.node.PositionNode;

public interface PositionListener extends EventListener {

	void onNodeSpawned(PositionNode node);

	void onNodeDecayed(PositionNode node);
}
