package com.acme.gameserver.packet.outbound;

import com.acme.core.ashley.Wired;
import com.acme.core.network.OutboundPacket;
import com.acme.gameserver.component.InventoryComponent;
import com.acme.gameserver.component.PlayerComponent;
import com.acme.gameserver.component.PositionComponent;
import com.acme.gameserver.component.TypeComponent;
import com.acme.gameserver.entity.Type;
import com.acme.gameserver.packet.OpCodes;
import com.acme.gameserver.util.TypeUtils;
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

        if (TypeUtils.isPlayer(entity)) {
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
