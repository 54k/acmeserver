package com.acme.server.packets.outbound;

import com.acme.ecs.core.ComponentMapper;
import com.acme.ecs.core.Entity;
import com.acme.ecs.core.Wire;
import com.acme.commons.network.OutboundPacket;
import com.acme.server.entities.EntityType;
import com.acme.server.entities.Type;
import com.acme.server.inventory.Inventory;
import com.acme.server.managers.PlayerComponent;
import com.acme.server.packets.OpCodes;
import com.acme.server.position.Transform;
import com.acme.server.utils.TypeUtils;

public class SpawnPacket extends OutboundPacket {

    @Wire
    private ComponentMapper<PlayerComponent> pcm;
    @Wire
    private ComponentMapper<Transform> poscm;
    @Wire
    private ComponentMapper<EntityType> tcm;
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
        Transform transform = poscm.get(entity);
        writeInt(transform.getX());
        writeInt(transform.getY());

        if (TypeUtils.isPlayer(entity)) {
            PlayerComponent playerComponent = pcm.get(entity);
            writeString(playerComponent.getName());
            writeInt(transform.getOrientation().getValue());
            Inventory inventory = icm.get(entity);
            writeInt(inventory.getArmor());
            writeInt(inventory.getWeapon());
        } else {
            writeInt(transform.getOrientation().getValue());
        }
    }
}
