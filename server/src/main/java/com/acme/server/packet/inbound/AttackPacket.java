package com.acme.server.packet.inbound;

import com.acme.commons.ashley.Wired;
import com.acme.commons.network.InboundPacket;
import com.acme.server.controller.CombatController;

public class AttackPacket extends InboundPacket {

    @Wired
    private CombatController combatController;

    private long targetId;

    @Override
    public void read() {
        targetId = readInt();
    }

    @Override
    public void run() {
        combatController.engage(getClient(), targetId);
    }
}
