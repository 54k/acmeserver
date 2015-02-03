package com.acme.gameserver.packet.outbound;

import com.acme.core.network.OutboundPacket;
import com.acme.gameserver.packet.OpCodes;

public class HealthPacket extends OutboundPacket {

    private int amount;
    private boolean regen;

    public HealthPacket(int amount, boolean regen) {
        this.amount = amount;
        this.regen = regen;
    }

    @Override
    public void write() {
        writeInt(OpCodes.HEALTH);
        writeInt(amount);
        if (regen) {
            writeInt(1);
        }
    }
}
