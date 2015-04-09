package com.acme.server.world;

import com.acme.commons.collections.EntityList;
import com.acme.ecs.core.Entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Region {

    private final EntityList entities = new EntityList();
    private final Set<Region> surroundingRegions = new HashSet<>();

    private boolean active = true;

    public void addSurroundingRegion(Region region) {
        surroundingRegions.add(region);
    }

    public Set<Region> getSurroundingRegions() {
        return Collections.unmodifiableSet(surroundingRegions);
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
        //        if (entities.getPlayers().size() == 1) {
        //            activateRegion();
        //        }
    }

    private void activateRegion() {
        active = true;
    }

    public void removeEntity(Entity entity) {
        entities.add(entity);
        //        if (entities.getPlayers().isEmpty()) {
        //            deactivateRegion();
        //        }
    }

    private void deactivateRegion() {
        active = false;
    }

    public EntityList getEntities() {
        return entities;
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
