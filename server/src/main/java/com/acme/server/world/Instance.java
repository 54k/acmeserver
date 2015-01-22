package com.acme.server.world;

import com.acme.server.manager.EntityManager;
import com.badlogic.ashley.core.Entity;

import java.util.HashMap;
import java.util.Map;

public class Instance {

    private static final int REGION_WIDTH = 28;
    private static final int REGION_HEIGHT = 12;

    private int id;
    private World world;
    private int maxPlayers;

    private int regionOffset;
    private Map<Integer, Region> regions = new HashMap<>();

    private Map<Long, Entity> entitiesById = new HashMap<>();
    private Map<Long, Entity> playersById = new HashMap<>();

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
        if (entitiesById.containsKey(entity.getId())) {
            throw new IllegalArgumentException("Duplicate entity id " + entity.getId());
        }

        if (EntityManager.isPlayer(entity)) {
            addPlayer(entity);
        }
        entitiesById.put(entity.getId(), entity);
    }

    public void removeEntity(Entity entity) {
        if (EntityManager.isPlayer(entity)) {
            removePlayer(entity);
        }
        entitiesById.remove(entity.getId(), entity);
    }

    public Entity findEntity(long id) {
        return entitiesById.get(id);
    }

    public Map<Long, Entity> getEntities() {
        return entitiesById;
    }

    private void addPlayer(Entity player) {
        playersById.put(player.getId(), player);
    }

    private void removePlayer(Entity player) {
        playersById.remove(player.getId());
    }

    public Map<Long, Entity> getPlayers() {
        return playersById;
    }
}
