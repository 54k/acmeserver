package com.acme.server.utils;

import com.acme.commons.utils.collections.EntityList;
import com.acme.commons.utils.collections.Predicates;
import com.acme.ecs.core.Aspect;
import com.acme.ecs.core.Entity;
import com.acme.server.entities.EntityBuilders;

import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class EntityContainer extends EntityList {

    private static final Collector<Entity, ?, EntityContainer> collector = Collectors.toCollection(EntityContainer::new);
    private static Aspect playerAspect = EntityBuilders.PLAYER_TYPE.getAspect();

    public Optional<Entity> getEntity(long id) {
        return Optional.ofNullable(querySingle(Predicates.id(id)));
    }

    public EntityContainer getPlayers() {
        return stream()
                .filter(playerAspect::matches)
                .collect(collector);
    }

    public Optional<Entity> getPlayer(long id) {
        return stream()
                .filter(playerAspect::matches)
                .filter(e -> e.getId() == id)
                .findFirst();
    }

    public boolean containsId(long id) {
        return getEntity(id).isPresent();
    }
}
