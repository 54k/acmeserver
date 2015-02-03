package com.acme.gameserver.packet.outbound;

import com.acme.core.ashley.Wired;
import com.acme.core.network.OutboundPacket;
import com.acme.gameserver.component.PositionComponent;
import com.acme.gameserver.packet.OpCodes;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

public class MovePacket extends OutboundPacket {

    @Wired
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
