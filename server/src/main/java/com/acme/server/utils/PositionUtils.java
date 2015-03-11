package com.acme.server.utils;

import com.acme.ecs.core.ComponentMapper;
import com.acme.ecs.core.Entity;
import com.acme.server.model.component.TransformComponent;
import com.acme.server.world.Area;
import com.acme.server.world.Position;

import java.util.Random;

public final class PositionUtils {

    private static ComponentMapper<TransformComponent> pcm = ComponentMapper.getFor(TransformComponent.class);

    private PositionUtils() {
    }

    public static boolean isInRange(Entity e1, Entity e2, int range) {
        Position pos1 = pcm.get(e1).position;
        Position pos2 = pcm.get(e2).position;
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
