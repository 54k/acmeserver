package com.acme.gameserver.packet.outbound;

import com.acme.core.network.OutboundPacket;
import com.acme.gameserver.packet.OpCodes;

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
