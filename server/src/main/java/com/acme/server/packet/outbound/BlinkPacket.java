package com.acme.server.packet.outbound;

import com.acme.engine.mechanics.network.OutboundPacket;
import com.acme.server.packet.OpCodes;

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
