package com.acme.server.controller;

import com.acme.engine.ecs.core.ComponentMapper;
import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.Wire;
import com.acme.engine.ecs.systems.PassiveSystem;
import com.acme.server.component.PositionComponent;
import com.acme.server.component.WorldComponent;
import com.acme.server.packet.outbound.MovePacket;
import com.acme.server.system.PacketSystem;
import com.acme.server.world.Position;
import com.acme.server.world.Region;

@Wire
public class PositionController extends PassiveSystem {

    private ComponentMapper<PositionComponent> positionCm;
    private ComponentMapper<WorldComponent> worldCm;

    private PacketSystem packetSystem;

    public Region getRegion(Entity entity) {
        return positionCm.get(entity).getRegion();
    }

    public Position getPosition(Entity entity) {
        return positionCm.get(entity).getPosition();
    }

    public void moveEntity(Entity entity, Position position) {
        updatePosition(entity, position);
        packetSystem.sendToSelfAndRegion(entity, new MovePacket(entity));
    }

    public void updatePosition(Entity entity, Position position) {
        PositionComponent positionComponent = positionCm.get(entity);
        positionComponent.setPosition(position);
        updateRegion(entity, positionComponent.getPosition());
    }

    private void updateRegion(Entity entity, Position position) {
        WorldComponent worldComponent = worldCm.get(entity);
        PositionComponent positionComponent = positionCm.get(entity);
        Region oldRegion = positionComponent.getRegion();
        Region newRegion = worldComponent.getInstance().findRegion(position);

        if (oldRegion != newRegion) {
            oldRegion.removeEntity(entity);
            newRegion.addEntity(entity);
            positionComponent.setRegion(newRegion);
        }
    }
}
