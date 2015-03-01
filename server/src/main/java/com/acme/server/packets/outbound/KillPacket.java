package com.acme.server.packets.outbound;

import com.acme.engine.mechanics.network.OutboundPacket;
import com.acme.server.entities.Type;
import com.acme.server.packets.OpCodes;

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
