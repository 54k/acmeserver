package com.acme.server.packet.outbound;

import com.acme.commons.network.OutboundPacket;
import com.acme.server.packet.OpCodes;

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
