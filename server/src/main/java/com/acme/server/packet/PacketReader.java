package com.acme.server.packet;

import com.acme.commons.network.InboundPacket;
import com.badlogic.ashley.core.Entity;

public class PacketReader extends com.acme.commons.network.PacketReader {

    @Override
    public InboundPacket readPacket(Entity entity, Object[] data) {
        int opCode = (int) data[0];
        Class<? extends InboundPacket> packetPrototype = getPacketPrototype(opCode);
        if (packetPrototype == null) {
            return null;
        }
        try {
            return packetPrototype.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
