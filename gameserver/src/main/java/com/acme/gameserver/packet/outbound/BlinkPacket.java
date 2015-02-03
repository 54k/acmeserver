package com.acme.gameserver.packet.outbound;

import com.acme.core.network.OutboundPacket;
import com.acme.gameserver.packet.OpCodes;

public class BlinkPacket extends OutboundPacket {

    private long entityId;

    public BlinkPacket(long entityId) {
        this.entityId = entityId;
    }

    @Override
    public void write() {
        writeInt(OpCodes.BLINK);
        writeLong(entityId);
    }
}
