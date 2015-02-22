package com.acme.server.controller;

import com.acme.engine.aegis.core.ComponentMapper;
import com.acme.engine.aegis.core.Entity;
import com.acme.engine.aegis.core.Wired;
import com.acme.engine.aegis.systems.PassiveSystem;
import com.acme.server.component.PositionComponent;
import com.acme.server.component.WorldComponent;
import com.acme.server.packet.outbound.MovePacket;
import com.acme.server.system.PacketSystem;
import com.acme.server.world.Position;
import com.acme.server.world.Region;

@Wired
public class PositionController extends PassiveSystem {

    private ComponentMapper<PositionComponent> pcm;
    private ComponentMapper<WorldComponent> wcm;

    private PacketSystem packetSystem;

    public Position getPosition(Entity entity) {
        return pcm.get(entity).getPosition();
    }

    public void moveEntity(Entity entity, Position position) {
        updatePosition(entity, position);
        packetSystem.sendToSelfAndRegion(entity, new MovePacket(entity));
    }

    public void updatePosition(Entity entity, Position position) {
        PositionComponent positionComponent = pcm.get(entity);
        positionComponent.setPosition(position);
        updateRegion(entity, positionComponent.getPosition());
    }

    private void updateRegion(Entity entity, Position position) {
        WorldComponent worldComponent = wcm.get(entity);
        PositionComponent positionComponent = pcm.get(entity);
        Region oldRegion = positionComponent.getRegion();
        Region newRegion = worldComponent.getInstance().findRegion(position);

        if (oldRegion != newRegion) {
            oldRegion.removeEntity(entity);
            newRegion.addEntity(entity);
            positionComponent.setRegion(newRegion);
        }
    }
}
