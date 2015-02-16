package com.acme.server.util;

import com.acme.server.entities.Archetypes;
import com.badlogic.ashley.core.Entity;

public final class TypeUtils {
    private TypeUtils() {
    }

    public static boolean isPlayer(Entity entity) {
        return Archetypes.PLAYER_TYPE.getFamily().matches(entity);
    }

    public static boolean isCreature(Entity entity) {
        return Archetypes.CREATURE_TYPE.getFamily().matches(entity);
    }
}
