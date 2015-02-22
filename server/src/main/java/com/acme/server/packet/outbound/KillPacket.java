package com.acme.server.packet.outbound;

import com.acme.engine.mechanics.network.OutboundPacket;
import com.acme.server.entity.Type;
import com.acme.server.packet.OpCodes;

public class KillPacket extends OutboundPacket {

    private Type mobType;

    public KillPacket(Type mobType) {
        this.mobType = mobType;
    }

    @Override
    public void write() {
        writeInt(OpCodes.KILL);
        writeInt(mobType.getId());
    }
}
