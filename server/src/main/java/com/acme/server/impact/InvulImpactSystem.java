package com.acme.server.impact;

import com.acme.engine.aegis.core.Entity;
import com.acme.engine.aegis.core.Wired;
import com.acme.engine.impact.ImpactSystem;
import com.acme.server.combat.StatsController;
import com.acme.server.entity.Type;
import com.acme.server.inventory.InventoryController;
import com.acme.server.packet.outbound.EquipPacket;
import com.acme.server.packet.outbound.HealthPacket;
import com.acme.server.system.PacketSystem;

@Wired
public class InvulImpactSystem extends ImpactSystem<InvulImpact> {

    private StatsController statsController;
    private InventoryController inventoryController;
    private PacketSystem packetSystem;

    public InvulImpactSystem() {
        super(InvulImpact.class);
    }

    @Override
    protected void impactApplied(InvulImpact impact, Entity target) {
        statsController.resetHitPoints(target);
        packetSystem.sendToSelfAndRegion(target, new EquipPacket(target, Type.FIREFOX.getId()));
    }

    @Override
    protected void impactRemoved(InvulImpact impact, Entity target) {
        int armor = inventoryController.getEquippedArmor(target);
        packetSystem.sendToSelfAndRegion(target, new EquipPacket(target, armor));
        packetSystem.sendPacket(target, new HealthPacket(statsController.getMaxHitPoints(target), true));
    }
}
