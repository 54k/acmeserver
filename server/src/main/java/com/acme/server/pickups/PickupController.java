package com.acme.server.pickups;

import com.acme.engine.ashley.Wired;
import com.acme.engine.ashley.system.ManagerSystem;
import com.acme.engine.effects.EffectSystem;
import com.acme.server.controller.DropController;
import com.acme.server.controller.InventoryController;
import com.acme.server.controller.StatsController;
import com.acme.server.effects.EffectFactory;
import com.acme.server.entities.EntityFactory;
import com.acme.server.entities.Type;
import com.acme.server.manager.WorldManager;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

@Wired
public class PickupController extends ManagerSystem {

    private ComponentMapper<Pickup> pickupCm;

    private InventoryController inventoryController;
    private StatsController statsController;
    private DropController dropController;

    private EntityFactory entityFactory;
    private WorldManager worldManager;
    private EffectFactory effectFactory;

    private EffectSystem effectSystem;

    public void gatherPickup(Entity entity, Entity item) {
        Pickup pickup = pickupCm.get(item);
        Type type = entityFactory.getType(item);

        boolean shouldDecayPickup = false;
        switch (pickup.getPickupType()) {
            case ARMOR:
                shouldDecayPickup = inventoryController.tryEquipArmor(entity, type.getId());
                break;
            case WEAPON:
                shouldDecayPickup = inventoryController.tryEquipWeapon(entity, type.getId());
                break;
            case HEALTH_POTION:
                Entity healthPotionEffect = effectFactory.createHealthPotionEffect(pickup.getAmount());
                applyEffect(healthPotionEffect, entity);
                shouldDecayPickup = true;
                break;
            case FIREFOX_POTION:
                Entity fireFoxPotionEffect = effectFactory.createFireFoxPotionEffect(pickup.getAmount());
                applyEffect(fireFoxPotionEffect, entity);
                shouldDecayPickup = true;
                break;
        }
        if (shouldDecayPickup) {
            worldManager.decay(item);
        }
    }

    private void applyEffect(Entity effect, Entity target) {
        effectSystem.applyEffect(effect, target);
    }

    public void openChest(Entity chest) {
        dropController.dropItems(chest);
        worldManager.decay(chest);
    }
}
