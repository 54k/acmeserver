package com.acme.engine.network;

import com.badlogic.ashley.core.Entity;

import java.util.HashMap;
import java.util.Map;

public abstract class PacketReader {

    private final Map<Object, Class<? extends InboundPacket>> packetsByOpCode = new HashMap<>();

    public PacketReader registerPacketPrototype(Object opCode, Class<? extends InboundPacket> packetPrototype) {
        packetsByOpCode.put(opCode, packetPrototype);
        return this;
    }

    public Class<? extends InboundPacket> getPacketPrototype(Object opCode) {
        return packetsByOpCode.get(opCode);
    }

    public abstract InboundPacket readPacket(Entity entity, Object[] data);
}
