package com.acme.server.manager;

import com.acme.commons.ashley.ManagerSystem;
import com.acme.commons.ashley.Wired;
import com.acme.server.component.PositionComponent;
import com.acme.server.component.WorldComponent;
import com.acme.server.packet.outbound.MovePacket;
import com.acme.server.system.GameServerNetworkSystem;
import com.acme.server.world.Position;
import com.acme.server.world.Region;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

@Wired
public class PositionManager extends ManagerSystem {

    private ComponentMapper<PositionComponent> pcm;
    private ComponentMapper<WorldComponent> wcm;

    private GameServerNetworkSystem networkSystem;

    public void moveEntity(Entity entity, Position position) {
        updatePosition(entity, position);
        networkSystem.sendToSelfAndRegion(entity, new MovePacket(entity));
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
