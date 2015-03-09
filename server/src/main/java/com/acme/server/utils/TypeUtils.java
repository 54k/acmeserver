package com.acme.server.utils;

import com.acme.ecs.core.Entity;
import com.acme.server.entities.EntityBuilders;

public final class TypeUtils {
    private TypeUtils() {
    }

    public static boolean isPlayer(Entity entity) {
        return EntityBuilders.PLAYER_TYPE.getFamily().matches(entity);
    }

    public static boolean isCreature(Entity entity) {
        return EntityBuilders.CREATURE_TYPE.getFamily().matches(entity);
    }
}
