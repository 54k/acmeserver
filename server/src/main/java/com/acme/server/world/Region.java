package com.acme.server.world;

import com.acme.server.util.EntityContainer;
import com.badlogic.ashley.core.Entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Region {

    private final EntityContainer entities = new EntityContainer();
    private final Set<Region> surroundingRegions = new HashSet<>();

    private boolean active;

    public void addSurroundingRegion(Region region) {
        surroundingRegions.add(region);
    }

    public Set<Region> getSurroundingRegions() {
        return Collections.unmodifiableSet(surroundingRegions);
    }

    public void addEntity(Entity entity) {
        entities.addEntity(entity);
        if (entities.getPlayers().size() == 1) {
            activateRegion();
        }
    }

    private void activateRegion() {
        active = true;
    }

    public void removeEntity(Entity entity) {
        entities.removeEntity(entity);
        if (entities.getPlayers().isEmpty()) {
            deactivateRegion();
        }
    }

    private void deactivateRegion() {
        active = false;
    }

    public Map<Long, Entity> getEntities() {
        return entities.getEntities();
    }

    public Map<Long, Entity> getPlayers() {
        return entities.getPlayers();
    }

    public boolean isActive() {
        for (Region surroundingRegion : surroundingRegions) {
            if (surroundingRegion.isRegionActive()) {
                return true;
            }
        }
        return false;
    }

    public boolean isRegionActive() {
        return active;
    }
}
