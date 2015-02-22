package com.acme.server.packet.outbound;

import com.acme.engine.ecs.core.ComponentMapper;
import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.Wire;
import com.acme.engine.mechanics.network.OutboundPacket;
import com.acme.server.component.PositionComponent;
import com.acme.server.packet.OpCodes;

@Wire
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
