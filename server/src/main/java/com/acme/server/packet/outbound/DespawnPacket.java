package com.acme.server.packet.outbound;

import com.acme.engine.aegis.core.Entity;
import com.acme.engine.network.OutboundPacket;
import com.acme.server.packet.OpCodes;

public class DespawnPacket extends OutboundPacket {

    private Entity entity;

    public DespawnPacket(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void write() {
        writeInt(OpCodes.DESPAWN);
        writeLong(entity.getId());
    }
}
