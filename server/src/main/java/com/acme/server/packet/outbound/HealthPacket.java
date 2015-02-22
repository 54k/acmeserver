package com.acme.server.packet.outbound;

import com.acme.engine.mechanics.network.OutboundPacket;
import com.acme.server.packet.OpCodes;

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
