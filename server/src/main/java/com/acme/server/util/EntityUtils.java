package com.acme.server.util;

import com.acme.server.entity.Archetypes;
import com.badlogic.ashley.core.Entity;

public final class EntityUtils {
    private EntityUtils() {
    }

    public static boolean isPlayer(Entity entity) {
        return Archetypes.PLAYER_TYPE.getFamily().matches(entity);
    }
}
