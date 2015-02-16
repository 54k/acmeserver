package com.acme.server.effects;

import com.acme.engine.ashley.Wired;
import com.acme.engine.effects.Effect;
import com.acme.engine.effects.EffectSystem;
import com.acme.engine.effects.ImpactController;
import com.acme.server.component.InvulnerableComponent;
import com.acme.server.controller.InventoryController;
import com.acme.server.controller.StatsController;
import com.acme.server.entity.Type;
import com.acme.server.packet.outbound.EquipPacket;
import com.acme.server.system.PacketSystem;
import com.badlogic.ashley.core.Entity;

@Wired
public final class InvulImpactController extends ImpactController {

    private InventoryController inventoryController;
    private StatsController statsController;

    private EffectSystem effectSystem;
    private PacketSystem packetSystem;

    public InvulImpactController() {
        super(InvulImpact.class);
    }

    @Override
    public void stacked(Entity effect, Entity target) {
        Effect effectCmp = effectSystem.getEffectInfo(effect);
        Entity other = effectSystem.getEffect(effectCmp.getIdentity(), target);
        Effect otherEffectCmp = effectSystem.getEffectInfo(other);
        otherEffectCmp.setTimeToNextTick(effectCmp.getTimeToNextTick());
    }

    @Override
    public void applied(Entity effect, Entity target) {
        target.add(new InvulnerableComponent(0));
        statsController.addHitPoints(target, statsController.getMaxHitPoints(target), true);
        packetSystem.sendToSelfAndRegion(target, new EquipPacket(target, Type.FIREFOX.getId()));
    }

    @Override
    public void removed(Entity effect, Entity target) {
        target.remove(InvulnerableComponent.class);
        int armor = inventoryController.getEquippedArmor(target);
        packetSystem.sendToSelfAndRegion(target, new EquipPacket(target, armor));
    }
}
