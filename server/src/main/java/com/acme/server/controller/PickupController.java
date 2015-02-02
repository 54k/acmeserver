package com.acme.server.controller;

import com.acme.commons.ashley.ManagerSystem;
import com.acme.commons.ashley.Wired;
import com.acme.server.component.InvulnerableComponent;
import com.acme.server.component.PickupComponent;
import com.acme.server.component.TypeComponent;
import com.acme.server.component.WorldComponent;
import com.acme.server.manager.WorldManager;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

@Wired
public class PickupController extends ManagerSystem {

    private ComponentMapper<WorldComponent> wcm;
    private ComponentMapper<PickupComponent> pcm;
    private ComponentMapper<TypeComponent> icm;

    private InventoryController inventoryController;
    private StatsController statsController;
    private DropController dropController;

    private WorldManager worldManager;

    public void gatherPickup(Entity entity, Entity item) {
        PickupComponent pickupComponent = pcm.get(item);
        TypeComponent typeComponent = icm.get(item);

        boolean shouldDecayPickup = false;
        switch (pickupComponent.getPickupType()) {
            case ARMOR:
                shouldDecayPickup = inventoryController.tryEquipArmor(entity, typeComponent.getType().getId());
                break;
            case WEAPON:
                shouldDecayPickup = inventoryController.tryEquipWeapon(entity, typeComponent.getType().getId());
                break;
            case HEALTH_POTION:
                statsController.addHitPoints(entity, pickupComponent.getAmount());
                shouldDecayPickup = true;
                break;
            case FIREFOX_POTION:
                shouldDecayPickup = true;
                InvulnerableComponent component = new InvulnerableComponent();
                component.setCooldown(pickupComponent.getAmount());
                entity.add(component);
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
