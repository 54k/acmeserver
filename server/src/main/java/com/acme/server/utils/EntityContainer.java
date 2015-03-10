package com.acme.server.utils;

import com.acme.commons.utils.EntityList;
import com.acme.ecs.core.Entity;
import com.acme.ecs.core.Family;
import com.acme.server.entities.EntityBuilders;

import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class EntityContainer extends EntityList {

    private static final Collector<Entity, ?, EntityContainer> collector = Collectors.toCollection(EntityContainer::new);
    private static Family playerFamily = EntityBuilders.PLAYER_TYPE.getFamily();

    public Optional<Entity> getEntity(long id) {
        return Optional.ofNullable(getBy(id));
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
