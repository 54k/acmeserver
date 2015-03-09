package com.acme.server.packets.outbound;

import com.acme.commons.network.OutboundPacket;
import com.acme.server.packets.OpCodes;

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
