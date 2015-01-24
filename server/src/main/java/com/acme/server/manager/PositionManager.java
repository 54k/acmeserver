package com.acme.server.manager;

import com.acme.commons.ashley.ManagerSystem;
import com.acme.commons.ashley.Wired;
import com.acme.server.component.PositionComponent;
import com.acme.server.component.WorldComponent;
import com.acme.server.packet.outbound.MovePacket;
import com.acme.server.system.NetworkSystem;
import com.acme.server.world.Area;
import com.acme.server.world.Position;
import com.acme.server.world.Region;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

import java.util.Random;

@Wired
public class PositionManager extends ManagerSystem {

    private static ComponentMapper<PositionComponent> pcm = ComponentMapper.getFor(PositionComponent.class);

    private ComponentMapper<WorldComponent> wcm;
    private NetworkSystem networkSystem;

    public void updatePosition(Entity entity, Position position) {
        PositionComponent positionComponent = pcm.get(entity);
        positionComponent.setPosition(position);
        updateRegion(entity, positionComponent.getPosition());
        networkSystem.sendToKnownList(entity, new MovePacket(entity));
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

    public static boolean isInRange(Entity e1, Entity e2, int range) {
        Position pos1 = pcm.get(e1).getPosition();
        Position pos2 = pcm.get(e2).getPosition();
        int dx = Math.abs(pos1.getX() - pos2.getX());
        int dy = Math.abs(pos1.getY() - pos2.getY());
        return dx <= range && dy <= range;
    }

    public static boolean isOutOfRange(Entity e1, Entity e2, int range) {
        return !isInRange(e1, e2, range);
    }

    public static Position getRandomPositionInside(int x, int y, int width, int height) {
        Random random = new Random();
        return new Position(x + random.nextInt(width + 1), y + random.nextInt(height + 1));
    }

    public static Position getRandomPositionInside(Area area) {
        return getRandomPositionInside(area.getX(), area.getY(), area.getWidth(), area.getHeight());
    }
}
