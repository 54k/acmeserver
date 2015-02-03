package com.acme.gameserver.controller;

import com.acme.core.ashley.ManagerSystem;
import com.acme.core.ashley.Wired;
import com.acme.gameserver.component.PositionComponent;
import com.acme.gameserver.component.WorldComponent;
import com.acme.gameserver.packet.outbound.MovePacket;
import com.acme.gameserver.system.GsPacketSystem;
import com.acme.gameserver.world.Position;
import com.acme.gameserver.world.Region;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

@Wired
public class PositionController extends ManagerSystem {

    private ComponentMapper<PositionComponent> pcm;
    private ComponentMapper<WorldComponent> wcm;

    private GsPacketSystem gsPacketSystem;

    public void moveEntity(Entity entity, Position position) {
        updatePosition(entity, position);
        gsPacketSystem.sendToSelfAndRegion(entity, new MovePacket(entity));
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

    public Position getPosition(Entity entity) {
        return pcm.get(entity).getPosition();
    }
}
