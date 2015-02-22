package com.acme.server.inventory;

import com.acme.engine.aegis.core.ComponentMapper;
import com.acme.engine.aegis.core.Entity;
import com.acme.engine.aegis.core.Wired;
import com.acme.engine.aegis.systems.PassiveSystem;
import com.acme.server.packet.outbound.EquipPacket;
import com.acme.server.system.PacketSystem;

@Wired
public class InventoryController extends PassiveSystem {

    private ComponentMapper<Inventory> inventoryCm;

    private PacketSystem packetSystem;

    public boolean tryEquipWeapon(Entity entity, int weapon) {
        Inventory inventory = inventoryCm.get(entity);
        if (inventory.weapon < weapon) {
            inventory.weapon = weapon;
            packetSystem.sendToSelfAndRegion(entity, new EquipPacket(entity, weapon));
            return true;
        }
        return false;
    }

    public boolean tryEquipArmor(Entity entity, int armor) {
        Inventory inventory = inventoryCm.get(entity);
        if (inventory.armor < armor) {
            inventory.armor = armor;
            packetSystem.sendToSelfAndRegion(entity, new EquipPacket(entity, armor));
            return true;
        }
        return false;
    }

    public int getEquippedWeapon(Entity entity) {
        return inventoryCm.get(entity).getWeapon();
    }

    public int getEquippedArmor(Entity entity) {
        return inventoryCm.get(entity).getArmor();
    }
}
