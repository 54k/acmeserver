package com.acme.server.impacts;

import com.acme.ecs.core.Entity;
import com.acme.ecs.core.Wire;
import com.acme.commons.impact.ImpactSystem;
import com.acme.server.packets.PacketSystem;
import com.acme.server.packets.outbound.BlinkPacket;

@Wire
public class BlinkImpactSystem extends ImpactSystem<BlinkImpact> {

    private PacketSystem packetSystem;

    public BlinkImpactSystem() {
        super(BlinkImpact.class);
    }

    @Override
    protected void impactApplied(BlinkImpact impact, Entity target) {
        packetSystem.sendToSelfAndRegion(target, new BlinkPacket(target.getId()));
    }
}
