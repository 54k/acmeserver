package com.acme.server.util;

import com.badlogic.ashley.core.Entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class EntityContainer {

    private final Map<Long, Entity> entitiesById = new HashMap<>();
    private final Map<Long, Entity> playersById = new HashMap<>();

    public Entity addEntity(Entity entity) {
        if (EntityUtils.isPlayer(entity)) {
            addPlayer(entity);
        }
        return entitiesById.put(entity.getId(), entity);
    }

    private void addPlayer(Entity player) {
        playersById.put(player.getId(), player);
    }

    public boolean removeEntity(Entity entity) {
        if (EntityUtils.isPlayer(entity)) {
            removePlayer(entity);
        }
        return entitiesById.remove(entity.getId(), entity);
    }

    private void removePlayer(Entity player) {
        playersById.remove(player.getId());
    }

    public Entity findEntityById(long id) {
        return entitiesById.get(id);
    }

    public Entity findPlayerById(long id) {
        return playersById.get(id);
    }

    public Map<Long, Entity> getEntities() {
        return Collections.unmodifiableMap(entitiesById);
    }

    public Map<Long, Entity> getPlayers() {
        return Collections.unmodifiableMap(playersById);
    }

    public boolean contains(Entity entity) {
        return entitiesById.containsKey(entity.getId());
    }
}
