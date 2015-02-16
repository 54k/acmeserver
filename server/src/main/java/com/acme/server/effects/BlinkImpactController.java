package com.acme.server.effects;

import com.acme.engine.ashley.Wired;
import com.acme.engine.effects.ImpactController;
import com.acme.server.packet.outbound.BlinkPacket;
import com.acme.server.system.PacketSystem;
import com.badlogic.ashley.core.Entity;

@Wired
public class BlinkImpactController extends ImpactController {

    private PacketSystem packetSystem;

    public BlinkImpactController() {
        super(BlinkImpact.class);
    }

    @Override
    public void applied(Entity effect, Entity target) {
        packetSystem.sendToSelfAndRegion(target, new BlinkPacket(target.getId()));
    }
}
