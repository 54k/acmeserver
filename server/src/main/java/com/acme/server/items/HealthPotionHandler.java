package com.acme.server.items;

import com.acme.ecs.core.Entity;
import com.acme.ecs.core.Wire;
import com.acme.server.impacts.HealImpact;
import com.acme.server.managers.WorldManager;

@Wire
public class HealthPotionHandler implements ConsumableHandler {

    private WorldManager worldManager;

    private int healthAmount;

    public HealthPotionHandler(int healthAmount) {
        this.healthAmount = healthAmount;
    }

    @Override
    public void consume(Entity consumer, Entity consumable) {
        consumer.addComponent(new HealImpact(healthAmount / 5, 5, 500));
        worldManager.decay(consumable);
    }
}
