package com.acme.server.packets.outbound;

import com.acme.engine.mechanics.network.OutboundPacket;
import com.acme.server.packets.OpCodes;

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
