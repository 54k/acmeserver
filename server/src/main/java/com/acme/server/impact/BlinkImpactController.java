package com.acme.server.impact;

import com.acme.engine.ashley.Wired;
import com.acme.engine.impact.ImpactController;
import com.acme.server.packet.outbound.BlinkPacket;
import com.acme.server.system.PacketSystem;
import com.badlogic.ashley.core.Entity;

@Wired
public class BlinkImpactController extends ImpactController<BlinkImpact> {

    private PacketSystem packetSystem;

    public BlinkImpactController() {
        super(BlinkImpact.class);
    }

    @Override
    protected void impactApplied(BlinkImpact impact, Entity target) {
        packetSystem.sendToSelfAndRegion(target, new BlinkPacket(target.getId()));
    }
}
