package com.acme.server.packets.outbound;

import com.acme.engine.mechanics.network.OutboundPacket;
import com.acme.server.packets.OpCodes;

public class AttackPacket extends OutboundPacket {

    private long attackerId;
    private long targetId;

    public AttackPacket(long attackerId, long targetId) {
        this.attackerId = attackerId;
        this.targetId = targetId;
    }

    @Override
    public void write() {
        writeInt(OpCodes.ATTACK);
        writeLong(attackerId);
        writeLong(targetId);
    }
}
