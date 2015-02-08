package com.acme.server.controller;

import com.acme.engine.ashley.ManagerSystem;
import com.acme.engine.ashley.Wired;
import com.acme.server.component.InventoryComponent;
import com.acme.server.packet.outbound.EquipPacket;
import com.acme.server.system.PacketSystem;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

@Wired
public class InventoryController extends ManagerSystem {

    private ComponentMapper<InventoryComponent> icm;

    private PacketSystem packetSystem;

    public boolean tryEquipWeapon(Entity entity, int weapon) {
        InventoryComponent inventoryComponent = icm.get(entity);
        if (inventoryComponent.getWeapon() < weapon) {
            inventoryComponent.setWeapon(weapon);
            packetSystem.sendToSelfAndRegion(entity, new EquipPacket(entity, weapon));
            return true;
        }
        return false;
    }

    public boolean tryEquipArmor(Entity entity, int armor) {
        InventoryComponent inventoryComponent = icm.get(entity);
        if (inventoryComponent.getArmor() < armor) {
            inventoryComponent.setArmor(armor);
            packetSystem.sendToSelfAndRegion(entity, new EquipPacket(entity, armor));
            return true;
        }
        return false;
    }

    public int getEquippedWeapon(Entity entity) {
        return icm.get(entity).getWeapon();
    }

    public int getEquippedArmor(Entity entity) {
        return icm.get(entity).getArmor();
    }
}
