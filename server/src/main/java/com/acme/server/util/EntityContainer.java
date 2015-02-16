package com.acme.server.util;

import com.acme.server.entities.Archetypes;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class EntityContainer extends ArrayList<Entity> {

    public static final Collector<Entity, ?, EntityContainer> ENTITY_CONTAINER_COLLECTOR = Collectors.toCollection(EntityContainer::new);
    private static Family PLAYER_FAMILY = Archetypes.PLAYER_TYPE.getFamily();

    public Optional<Entity> getEntityById(long id) {
        return stream()
                .filter(e -> e.getId() == id)
                .findFirst();
    }

    public EntityContainer getPlayers() {
        return stream()
                .filter(PLAYER_FAMILY::matches)
                .collect(ENTITY_CONTAINER_COLLECTOR);
    }

    public Optional<Entity> getPlayerById(long id) {
        return stream()
                .filter(PLAYER_FAMILY::matches)
                .filter(e -> e.getId() == id)
                .findFirst();
    }

    public boolean containsById(long id) {
        return getEntityById(id).isPresent();
    }
}
