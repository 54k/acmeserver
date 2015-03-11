package com.acme.server.inventory;

import com.acme.ecs.core.ComponentMapper;
import com.acme.ecs.core.Entity;
import com.acme.ecs.core.Wire;
import com.acme.ecs.systems.PassiveSystem;
import com.acme.server.combat.StatsSystem;
import com.acme.server.entities.EntityFactory;
import com.acme.server.entities.Type;
import com.acme.server.impacts.HealImpact;
import com.acme.server.impacts.InvulImpact;
import com.acme.server.model.node.WorldNode;
import com.acme.server.model.system.WorldSystem;

@Wire
public class PickupSystem extends PassiveSystem {

    private ComponentMapper<Pickup> pickupCm;

    private InventorySystem inventory;
    private StatsSystem stats;
    private LootTableSystem drop;

    private EntityFactory entityFactory;
    private WorldSystem worldSystem;

    public void gatherPickup(Entity entity, Entity item) {
        Pickup pickup = pickupCm.get(item);
        Type type = entityFactory.getType(item);

        boolean shouldDecayPickup = false;
        switch (pickup.getPickupType()) {
            case ARMOR:
                shouldDecayPickup = inventory.tryEquipArmor(entity, type.getId());
                break;
            case WEAPON:
                shouldDecayPickup = inventory.tryEquipWeapon(entity, type.getId());
                break;
            case HEALTH_POTION:
                entity.addComponent(new HealImpact(pickup.getAmount() / 5, 5, 500));
                shouldDecayPickup = true;
                break;
            case FIREFOX_POTION:
                entity.addComponent(new InvulImpact(pickup.getAmount()));
                shouldDecayPickup = true;
                break;
        }
        if (shouldDecayPickup) {
            worldSystem.decay(item.getNode(WorldNode.class));
        }
    }

    public void openChest(Entity chest) {
        drop.dropItemsFrom(chest);
        worldSystem.decay(chest.getNode(WorldNode.class));
    }
}
