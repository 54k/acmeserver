package com.acme.gameserver.packet.outbound;

import com.acme.core.network.OutboundPacket;
import com.acme.gameserver.packet.OpCodes;

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
