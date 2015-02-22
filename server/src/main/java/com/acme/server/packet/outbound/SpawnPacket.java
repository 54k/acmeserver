package com.acme.server.packet.outbound;

import com.acme.engine.ecs.core.ComponentMapper;
import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.Wire;
import com.acme.engine.mechanics.network.OutboundPacket;
import com.acme.server.component.PlayerComponent;
import com.acme.server.component.PositionComponent;
import com.acme.server.component.TypeComponent;
import com.acme.server.entity.Type;
import com.acme.server.inventory.Inventory;
import com.acme.server.packet.OpCodes;
import com.acme.server.util.TypeUtils;

public class SpawnPacket extends OutboundPacket {

    @Wire
    private ComponentMapper<PlayerComponent> pcm;
    @Wire
    private ComponentMapper<PositionComponent> poscm;
    @Wire
    private ComponentMapper<TypeComponent> tcm;
    @Wire
    private ComponentMapper<Inventory> icm;

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
            Inventory inventory = icm.get(entity);
            writeInt(inventory.getArmor());
            writeInt(inventory.getWeapon());
        } else {
            writeInt(positionComponent.getOrientation().getValue());
        }
    }
}
