package com.acme.server.items;

import com.acme.ecs.core.Entity;
import com.acme.ecs.core.Wire;
import com.acme.server.impacts.HealImpact;
import com.acme.server.model.node.WorldNode;
import com.acme.server.model.system.WorldSystem;

public class HealthPotionHandler implements ConsumableHandler {

    @Wire
    private WorldSystem worldSystem;

    private int healthAmount;

    public HealthPotionHandler(int healthAmount) {
        this.healthAmount = healthAmount;
    }

    @Override
    public void consume(Entity consumer, Entity consumable) {
        consumer.addComponent(new HealImpact(healthAmount / 5, 5, 500));
        worldSystem.decay(consumable.getNode(WorldNode.class));
    }
}
