package com.acme.server.world;

import com.acme.server.manager.EntityManager;
import com.badlogic.ashley.core.Entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Region {

    private Map<Long, Entity> entities = new ConcurrentHashMap<>();
    private Map<Long, Entity> players = new ConcurrentHashMap<>();

    private Set<Region> surroundingRegions = new HashSet<>();

    private boolean active;

    public void addSurroundingRegion(Region region) {
        surroundingRegions.add(region);
    }

    public Set<Region> getSurroundingRegions() {
        return Collections.unmodifiableSet(surroundingRegions);
    }

    public void addEntity(Entity entity) {
        entities.put(entity.getId(), entity);
        if (EntityManager.isPlayer(entity)) {
            players.put(entity.getId(), entity);
            if (players.size() == 1) {
                activateRegion();
            }
        }
    }

    private void activateRegion() {
        active = true;
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity.getId());
        if (EntityManager.isPlayer(entity)) {
            players.remove(entity.getId());
            if (players.isEmpty()) {
                deactivateRegion();
            }
        }
    }

    private void deactivateRegion() {
        active = false;
    }

    public Map<Long, Entity> getEntities() {
        return Collections.unmodifiableMap(entities);
    }

    public Map<Long, Entity> getPlayers() {
        return Collections.unmodifiableMap(players);
    }

    public boolean isActive() {
        return active;
    }
}
