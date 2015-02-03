package com.acme.gameserver.packet.outbound;

import com.acme.core.network.OutboundPacket;
import com.acme.gameserver.entity.Type;
import com.acme.gameserver.packet.OpCodes;

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
