package com.acme.server.packets.outbound;

import com.acme.engine.ecs.core.Entity;
import com.acme.engine.mechanics.network.OutboundPacket;
import com.acme.server.packets.OpCodes;

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
