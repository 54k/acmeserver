package com.acme.server.utils;

import com.acme.ecs.core.Entity;
import com.acme.ecs.core.Family;
import com.acme.server.entities.EntityBuilders;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class EntityContainer extends ArrayList<Entity> {

    private static final Collector<Entity, ?, EntityContainer> collector = Collectors.toCollection(EntityContainer::new);
    private static Family playerFamily = EntityBuilders.PLAYER_TYPE.getFamily();

    public Optional<Entity> getEntity(long id) {
        return stream()
                .filter(e -> e.getId() == id)
                .findFirst();
    }

    public EntityContainer getPlayers() {
        return stream()
                .filter(playerFamily::matches)
                .collect(collector);
    }

    public Optional<Entity> getPlayer(long id) {
        return stream()
                .filter(playerFamily::matches)
                .filter(e -> e.getId() == id)
                .findFirst();
    }

    public boolean containsId(long id) {
        return getEntity(id).isPresent();
    }
}
