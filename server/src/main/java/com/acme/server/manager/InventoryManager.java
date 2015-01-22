package com.acme.server.manager;

import com.acme.commons.ashley.ManagerSystem;
import com.acme.commons.ashley.Wired;
import com.acme.server.component.InventoryComponent;
import com.acme.server.packet.outbound.EquipPacket;
import com.acme.server.system.NetworkSystem;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

@Wired
public class InventoryManager extends ManagerSystem {

    private ComponentMapper<InventoryComponent> icm;

    private NetworkSystem networkSystem;

    public boolean tryEquipWeapon(Entity entity, int weapon) {
        InventoryComponent inventoryComponent = icm.get(entity);
        if (inventoryComponent.getWeapon() < weapon) {
            inventoryComponent.setWeapon(weapon);
            networkSystem.sendToKnownList(entity, new EquipPacket(entity, weapon));
            return true;
        }
        return false;
    }

    public boolean tryEquipArmor(Entity entity, int armor) {
        InventoryComponent inventoryComponent = icm.get(entity);
        if (inventoryComponent.getArmor() < armor) {
            inventoryComponent.setArmor(armor);
            networkSystem.sendToKnownList(entity, new EquipPacket(entity, armor));
            return true;
        }
        return false;
    }
}
