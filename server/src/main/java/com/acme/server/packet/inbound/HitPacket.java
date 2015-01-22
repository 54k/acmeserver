package com.acme.server.packet.inbound;

import com.acme.commons.ashley.Wired;
import com.acme.commons.network.InboundPacket;
import com.acme.server.manager.CombatManager;

public class HitPacket extends InboundPacket {

    @Wired
    private CombatManager combatManager;

    private long targetId;

    @Override
    public void read() {
        targetId = readInt();
    }

    @Override
    public void run() {
        combatManager.attack(getClient(), targetId);
    }
}
