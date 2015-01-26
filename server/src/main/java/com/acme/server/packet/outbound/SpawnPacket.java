package com.acme.server.packet.outbound;

import com.acme.commons.ashley.Wired;
import com.acme.commons.network.OutboundPacket;
import com.acme.server.component.InventoryComponent;
import com.acme.server.component.PlayerComponent;
import com.acme.server.component.PositionComponent;
import com.acme.server.component.TypeComponent;
import com.acme.server.entity.Type;
import com.acme.server.packet.OpCodes;
import com.acme.server.util.EntityUtils;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

public class SpawnPacket extends OutboundPacket {

    @Wired
    private ComponentMapper<PlayerComponent> pcm;
    @Wired
    private ComponentMapper<PositionComponent> poscm;
    @Wired
    private ComponentMapper<TypeComponent> tcm;
    @Wired
    private ComponentMapper<InventoryComponent> icm;

    private Entity entity;

    public SpawnPacket(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void write() {
        writeInt(OpCodes.SPAWN);
        writeLong(entity.getId());
        Type type = tcm.get(entity).getType();
        writeInt(type.getId());
        PositionComponent positionComponent = poscm.get(entity);
        writeInt(positionComponent.getX());
        writeInt(positionComponent.getY());

        if (EntityUtils.isPlayer(entity)) {
            PlayerComponent playerComponent = pcm.get(entity);
            writeString(playerComponent.getName());
            writeInt(positionComponent.getOrientation().getValue());
            InventoryComponent inventoryComponent = icm.get(entity);
            writeInt(inventoryComponent.getArmor());
            writeInt(inventoryComponent.getWeapon());
        } else {
            writeInt(positionComponent.getOrientation().getValue());
        }
    }
}
