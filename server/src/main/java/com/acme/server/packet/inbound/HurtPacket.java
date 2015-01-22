package com.acme.server.packet.inbound;

import com.acme.commons.ashley.Wired;
import com.acme.commons.network.InboundPacket;
import com.acme.server.manager.CombatManager;

public class HurtPacket extends InboundPacket {

    @Wired
    private CombatManager combatManager;
    private long creatureId;

    @Override
    public void read() {
        creatureId = readInt();
    }

    @Override
    public void run() {
        combatManager.attack(creatureId, getClient());
    }
}
