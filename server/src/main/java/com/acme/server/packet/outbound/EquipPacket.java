package com.acme.server.packet.outbound;

import com.acme.commons.network.OutboundPacket;
import com.acme.server.packet.OpCodes;
import com.badlogic.ashley.core.Entity;

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
