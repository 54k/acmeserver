package com.acme.server.manager;

import com.acme.commons.ashley.ManagerSystem;
import com.acme.commons.ashley.Wired;
import com.acme.server.component.InvulnerableComponent;
import com.acme.server.component.PickupComponent;
import com.acme.server.component.TypeComponent;
import com.acme.server.component.WorldComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

@Wired
public class PickupManager extends ManagerSystem {

    private ComponentMapper<WorldComponent> wcm;
    private ComponentMapper<PickupComponent> pcm;
    private ComponentMapper<TypeComponent> icm;

    private WorldManager worldManager;
    private InventoryManager inventoryManager;
    private StatsManager statsManager;

    public void gatherPickup(Entity entity, long itemId) {
        WorldComponent worldComponent = wcm.get(entity);

        Entity item = worldComponent.getInstance().findEntity(itemId);
        PickupComponent pickupComponent = pcm.get(item);
        TypeComponent typeComponent = icm.get(item);

        boolean shouldDecayPickup = false;
        switch (pickupComponent.getPickupType()) {
            case ARMOR:
                shouldDecayPickup = inventoryManager.tryEquipArmor(entity, typeComponent.getType().getId());
                break;
            case WEAPON:
                shouldDecayPickup = inventoryManager.tryEquipWeapon(entity, typeComponent.getType().getId());
                break;
            case HEALTH_POTION:
                statsManager.addHitPoints(entity, pickupComponent.getAmount());
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
}
