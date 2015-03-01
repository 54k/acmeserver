package com.acme.server.packets.outbound;

import com.acme.engine.ecs.core.Entity;
import com.acme.engine.mechanics.network.OutboundPacket;
import com.acme.server.packets.OpCodes;

public class EquipPacket extends OutboundPacket {

    private Entity entity;
    private int itemType;

    public EquipPacket(Entity entity, int itemType) {
        this.entity = entity;
        this.itemType = itemType;
    }

    @Override
    public void write() {
        writeInt(OpCodes.EQUIP);
        writeLong(entity.getId());
        writeInt(itemType);
    }
}
