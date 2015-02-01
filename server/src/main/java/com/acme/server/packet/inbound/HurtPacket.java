package com.acme.server.packet.inbound;

import com.acme.commons.ashley.Wired;
import com.acme.commons.network.InboundPacket;
import com.acme.server.controller.CombatController;

public class HurtPacket extends InboundPacket {

    @Wired
    private CombatController combatController;
    private long creatureId;

    @Override
    public void read() {
        creatureId = readInt();
    }

    @Override
    public void run() {
        combatController.attack(creatureId, getClient());
    }
}
