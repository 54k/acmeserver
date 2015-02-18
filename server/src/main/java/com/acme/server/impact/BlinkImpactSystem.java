package com.acme.server.impact;

import com.acme.engine.processors.Wired;
import com.acme.engine.impact.ImpactSystem;
import com.acme.server.packet.outbound.BlinkPacket;
import com.acme.server.system.PacketSystem;
import com.badlogic.ashley.core.Entity;

@Wired
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
