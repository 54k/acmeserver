package com.acme.gameserver.packet.outbound;

import com.acme.core.network.OutboundPacket;
import com.acme.gameserver.packet.OpCodes;

public class DamagePacket extends OutboundPacket {

    private long targetId;
    private int damage;

    public DamagePacket(long targetId, int damage) {
        this.targetId = targetId;
        this.damage = damage;
    }

    @Override
    public void write() {
        writeInt(OpCodes.DAMAGE);
        writeLong(targetId);
        writeInt(damage);
    }
}
