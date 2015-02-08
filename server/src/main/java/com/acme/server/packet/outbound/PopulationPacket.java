package com.acme.server.packet.outbound;

import com.acme.engine.network.OutboundPacket;
import com.acme.server.packet.OpCodes;

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
