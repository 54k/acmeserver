package com.acme.server.impacts;

import com.acme.ecs.core.Entity;
import com.acme.ecs.core.Wire;
import com.acme.commons.impact.ImpactSystem;
import com.acme.server.combat.StatsSystem;
import com.acme.server.entities.Type;
import com.acme.server.inventory.InventorySystem;
import com.acme.server.packets.PacketSystem;
import com.acme.server.packets.outbound.EquipPacket;
import com.acme.server.packets.outbound.HealthPacket;

@Wire
public class InvulImpactSystem extends ImpactSystem<InvulImpact> {

    private StatsSystem statsSystem;
    private InventorySystem inventorySystem;
    private PacketSystem packetSystem;

    public InvulImpactSystem() {
        super(InvulImpact.class);
    }

    @Override
    protected void impactApplied(InvulImpact impact, Entity target) {
        statsSystem.resetHitPoints(target);
        packetSystem.sendToSelfAndRegion(target, new EquipPacket(target, Type.FIREFOX.getId()));
    }

    @Override
    protected void impactRemoved(InvulImpact impact, Entity target) {
        int armor = inventorySystem.getEquippedArmor(target);
        packetSystem.sendToSelfAndRegion(target, new EquipPacket(target, armor));
        packetSystem.sendPacket(target, new HealthPacket(statsSystem.getMaxHitPoints(target), true));
    }
}
