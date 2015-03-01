package com.acme.server.position;

import com.acme.engine.ecs.core.ComponentMapper;
import com.acme.engine.ecs.core.Entity;
import com.acme.engine.ecs.core.Wire;
import com.acme.engine.ecs.systems.PassiveSystem;
import com.acme.server.managers.WorldComponent;
import com.acme.server.packets.PacketSystem;
import com.acme.server.packets.outbound.MovePacket;
import com.acme.server.world.Position;
import com.acme.server.world.Region;

@Wire
public class TransformSystem extends PassiveSystem {

    private ComponentMapper<Transform> positionCm;
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
        Transform transform = positionCm.get(entity);
        transform.setPosition(position);
        updateRegion(entity, transform.getPosition());
    }

    private void updateRegion(Entity entity, Position position) {
        WorldComponent worldComponent = worldCm.get(entity);
        Transform transform = positionCm.get(entity);
        Region oldRegion = transform.getRegion();
        Region newRegion = worldComponent.getInstance().findRegion(position);

        if (oldRegion != newRegion) {
            oldRegion.removeEntity(entity);
            newRegion.addEntity(entity);
            transform.setRegion(newRegion);
        }
    }
}
