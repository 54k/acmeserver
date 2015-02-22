package com.acme.server.packet.outbound;

import com.acme.engine.mechanics.network.OutboundPacket;
import com.acme.server.packet.OpCodes;

public class HitPointsPacket extends OutboundPacket {

    private int maxHitPoints;

    public HitPointsPacket(int maxHitPoints) {
        this.maxHitPoints = maxHitPoints;
    }

    @Override
    public void write() {
        writeInt(OpCodes.HP);
        writeInt(maxHitPoints);
    }
}
