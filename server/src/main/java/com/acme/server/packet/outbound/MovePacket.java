package com.acme.server.packet.outbound;

import com.acme.commons.ashley.Wired;
import com.acme.commons.network.OutboundPacket;
import com.acme.server.component.PositionComponent;
import com.acme.server.packet.OpCodes;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

@Wired
public class MovePacket extends OutboundPacket {

    private ComponentMapper<PositionComponent> pcm;

    private Entity entity;

    public MovePacket(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void write() {
        writeInt(OpCodes.MOVE);
        writeLong(entity.getId());
        PositionComponent positionComponent = pcm.get(entity);
        writeInt(positionComponent.getX());
        writeInt(positionComponent.getY());
    }
}
