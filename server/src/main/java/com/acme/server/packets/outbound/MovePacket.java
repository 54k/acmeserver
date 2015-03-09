package com.acme.server.packets.outbound;

import com.acme.ecs.core.ComponentMapper;
import com.acme.ecs.core.Entity;
import com.acme.ecs.core.Wire;
import com.acme.commons.network.OutboundPacket;
import com.acme.server.packets.OpCodes;
import com.acme.server.position.Transform;

@Wire
public class MovePacket extends OutboundPacket {

    private ComponentMapper<Transform> pcm;

    private Entity entity;

    public MovePacket(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void write() {
        writeInt(OpCodes.MOVE);
        writeLong(entity.getId());
        Transform transform = pcm.get(entity);
        writeInt(transform.getX());
        writeInt(transform.getY());
    }
}
