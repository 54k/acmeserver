package com.acme.server.packet.outbound;

import com.acme.engine.network.OutboundPacket;
import com.acme.server.packet.OpCodes;
import com.badlogic.ashley.core.Entity;

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
