package com.acme.server.packets.outbound;

import com.acme.commons.network.OutboundPacket;
import com.acme.server.packets.OpCodes;

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
