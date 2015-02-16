package com.acme.server.controller;

import com.acme.engine.ashley.Wired;
import com.acme.engine.ashley.system.ManagerSystem;
import com.acme.engine.effects.EffectSystem;
import com.acme.server.component.PickupComponent;
import com.acme.server.effects.EffectFactory;
import com.acme.server.entity.Type;
import com.acme.server.manager.EntityManager;
import com.acme.server.manager.WorldManager;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

@Wired
public class PickupController extends ManagerSystem {

    private ComponentMapper<PickupComponent> pcm;

    private InventoryController inventoryController;
    private StatsController statsController;
    private DropController dropController;

    private EntityManager entityManager;
    private WorldManager worldManager;
    private EffectFactory effectFactory;

    private EffectSystem effectSystem;

    public void gatherPickup(Entity entity, Entity item) {
        PickupComponent pickupComponent = pcm.get(item);
        Type type = entityManager.getType(item);

        boolean shouldDecayPickup = false;
        switch (pickupComponent.getPickupType()) {
            case ARMOR:
                shouldDecayPickup = inventoryController.tryEquipArmor(entity, type.getId());
                break;
            case WEAPON:
                shouldDecayPickup = inventoryController.tryEquipWeapon(entity, type.getId());
                break;
            case HEALTH_POTION:
                statsController.addHitPoints(entity, pickupComponent.getAmount());
                shouldDecayPickup = true;
                break;
            case FIREFOX_POTION:
                shouldDecayPickup = true;
                effectSystem.applyEffect(effectFactory.createInvulEffect(10000), entity);
                break;
        }
        if (shouldDecayPickup) {
            worldManager.decay(item);
        }
    }

    public void openChest(Entity chest) {
        dropController.dropItems(chest);
        worldManager.decay(chest);
    }
}
