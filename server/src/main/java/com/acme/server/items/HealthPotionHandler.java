package com.acme.server.items;

import com.acme.ecs.core.Entity;
import com.acme.ecs.core.Wire;
import com.acme.server.impacts.HealImpact;
import com.acme.server.model.node.PositionNode;
import com.acme.server.model.node.WorldNode;
import com.acme.server.model.system.passive.PositionSystem;
import com.acme.server.model.system.passive.WorldSystem;

public class HealthPotionHandler implements ConsumableHandler {

	@Wire
	private WorldSystem worldSystem;
	@Wire
	private PositionSystem positionSystem;

	private int healthAmount;

	public HealthPotionHandler(int healthAmount) {
		this.healthAmount = healthAmount;
	}

	@Override
	public void consume(Entity consumer, Entity consumable) {
		consumer.addComponent(new HealImpact(healthAmount / 5, 5, 500));
		positionSystem.decay(consumable.getNode(PositionNode.class));
		worldSystem.removeFromWorld(consumable.getNode(WorldNode.class));
	}
}
