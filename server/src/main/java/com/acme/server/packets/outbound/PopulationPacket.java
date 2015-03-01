package com.acme.server.packets.outbound;

import com.acme.engine.mechanics.network.OutboundPacket;
import com.acme.server.packets.OpCodes;

public class PopulationPacket extends OutboundPacket {

    private final int instanceCount;
    private final int worldCount;

    public PopulationPacket(int instanceCount, int worldCount) {
        this.instanceCount = instanceCount;
        this.worldCount = worldCount;
    }

    @Override
    public void write() {
        writeInt(OpCodes.POPULATION);
        writeInt(instanceCount);
        writeInt(worldCount);
    }
}
