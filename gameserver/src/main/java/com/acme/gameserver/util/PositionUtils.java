package com.acme.gameserver.util;

import com.acme.gameserver.component.PositionComponent;
import com.acme.gameserver.world.Area;
import com.acme.gameserver.world.Position;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

import java.util.Random;

public final class PositionUtils {

    private static ComponentMapper<PositionComponent> pcm = ComponentMapper.getFor(PositionComponent.class);

    private PositionUtils() {
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
