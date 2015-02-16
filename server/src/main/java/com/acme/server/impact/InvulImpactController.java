package com.acme.server.impact;

import com.acme.engine.ashley.Wired;
import com.acme.engine.impact.ImpactController;
import com.acme.server.combat.StatsController;
import com.acme.server.component.InvulnerableComponent;
import com.acme.server.entity.Type;
import com.acme.server.inventory.InventoryController;
import com.acme.server.packet.outbound.EquipPacket;
import com.acme.server.packet.outbound.HealthPacket;
import com.acme.server.system.PacketSystem;
import com.badlogic.ashley.core.Entity;

@Wired
public class InvulImpactController extends ImpactController<InvulImpact> {

    private InventoryController inventoryController;
    private StatsController statsController;

    private PacketSystem packetSystem;

    public InvulImpactController() {
        super(InvulImpact.class);
    }

    @Override
    protected void impactApplied(InvulImpact impact, Entity target) {
        target.add(new InvulnerableComponent(0));
        statsController.resetHitPoints(target);
        packetSystem.sendToSelfAndRegion(target, new EquipPacket(target, Type.FIREFOX.getId()));
    }

    @Override
    protected void impactRemoved(InvulImpact impact, Entity target) {
        target.remove(InvulnerableComponent.class);
        int armor = inventoryController.getEquippedArmor(target);
        packetSystem.sendToSelfAndRegion(target, new EquipPacket(target, armor));
        packetSystem.sendPacket(target, new HealthPacket(statsController.getMaxHitPoints(target), true));
    }
}
