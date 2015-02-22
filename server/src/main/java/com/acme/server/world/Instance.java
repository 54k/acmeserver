package com.acme.server.world;

import com.acme.engine.ecs.core.Entity;
import com.acme.server.util.EntityContainer;

import java.util.HashMap;
import java.util.Map;

public class Instance {

    private static final int REGION_WIDTH = 28;
    private static final int REGION_HEIGHT = 12;

    private final int id;
    private final World world;
    private final int maxPlayers;

    private final int regionOffset;
    private final Map<Integer, Region> regions = new HashMap<>();

    private final EntityContainer entities = new EntityContainer();

    public Instance(int id, World world, int maxPlayers) {
        this.id = id;
        this.world = world;
        this.maxPlayers = maxPlayers;
        regionOffset = world.getHeight() / REGION_HEIGHT + 1;
        initRegions();
    }

    private void initRegions() {
        int width = world.getWidth();
        int height = world.getHeight();

        for (int i = 0; i < width; i += REGION_WIDTH) {
            for (int j = 0; j < height; j += REGION_HEIGHT) {
                int regionId = getRegionId(i, j);
                regions.put(regionId, new Region());
            }
        }

        for (int i = 0; i < width; i += REGION_WIDTH) {
            for (int j = 0; j < height; j += REGION_HEIGHT) {
                addSurroundingRegions(i, j);
            }
        }
    }

    private int getRegionId(int x, int y) {
        return (x + 1) / REGION_WIDTH * regionOffset + (y + 1) / REGION_HEIGHT;
    }

    private void addSurroundingRegions(int x, int y) {
        Region region = regions.get(getRegionId(x, y));
        for (int i = x - REGION_WIDTH; i <= x + REGION_WIDTH; i += REGION_WIDTH) {
            for (int j = y - REGION_HEIGHT; j <= y + REGION_HEIGHT; j += REGION_HEIGHT) {
                if (isValidRegionPosition(i, j)) {
                    Region sr = regions.get(getRegionId(i, j));
                    region.addSurroundingRegion(sr);
                }
            }
        }
    }

    private boolean isValidRegionPosition(int x, int y) {
        return !world.isOutOfBounds(x, y);
    }

    public int getId() {
        return id;
    }

    public World getWorld() {
        return world;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public Region findRegion(Position position) {
        return findRegion(position.getX(), position.getY());
    }

    public Region findRegion(int x, int y) {
        return regions.get(getRegionId(x, y));
    }

    public Map<Integer, Region> getRegions() {
        return regions;
    }

    public void addEntity(Entity entity) {
        if (entities.containsById(entity.getId())) {
            throw new IllegalArgumentException("Duplicate entity id " + entity.getId());
        }
        entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    public Entity getEntityById(long id) {
        return entities.getEntityById(id).orElse(null);
    }

    public EntityContainer getPlayers() {
        return entities.getPlayers();
    }

    public EntityContainer getEntities() {
        return entities;
    }

    public Entity getPlayerById(long id) {
        return entities.getPlayerById(id).orElse(null);
    }
}
