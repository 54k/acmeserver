package com.acme.server.world;

import com.acme.server.entities.Type;
import com.acme.server.template.CheckpointTemplate;
import com.acme.server.template.WorldTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class World {

    private WorldTemplate template;

    private Map<Integer, Area> playerStartingAreas = new HashMap<>();
    private Map<Integer, Area> playerSpawnAreas = new HashMap<>();

    private Map<Position, Type> staticObjectSpawns = new HashMap<>();

    private int[][] collisionGrid;

    private int instanceCount;
    private Map<Integer, Instance> instancesById = new HashMap<>();

    public World(WorldTemplate template) {
        this.template = template;
        initPlayerSpawns();
        initCollisionGrid();
        initStaticObjects();
    }

    private void initPlayerSpawns() {
        for (CheckpointTemplate checkpoint : template.getCheckpoints()) {
            Area area = new Area(checkpoint.getX(), checkpoint.getY(), checkpoint.getW(), checkpoint.getH());
            if (checkpoint.getS() == 1) {
                playerStartingAreas.put(checkpoint.getId(), area);
            } else {
                playerSpawnAreas.put(checkpoint.getId(), area);
            }
        }
    }

    private void initCollisionGrid() {
        int tileIndex = 0;
        int height = template.getHeight();
        int width = template.getWidth();

        collisionGrid = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                collisionGrid[i][j] = template.getCollisions().contains(tileIndex) ? 1 : 0;
                tileIndex++;
            }
        }
    }

    private void initStaticObjects() {
        for (Entry<Integer, Type> staticObjectEntry : template.getStaticEntities().entrySet()) {
            staticObjectSpawns.put(getPositionFromTileIndex(staticObjectEntry.getKey()), staticObjectEntry.getValue());
        }
    }

    private Position getPositionFromTileIndex(int tileIndex) {
        int width = template.getWidth();
        int x = (tileIndex % width == 0) ? width - 1 : (tileIndex % width);
        int y = (int) Math.floor(tileIndex / width);
        return new Position(x, y);
    }

    public WorldTemplate getTemplate() {
        return template;
    }

    public boolean isOutOfBounds(int x, int y) {
        return x < 0 || x > template.getWidth() || y < 0 || y > template.getHeight();
    }

    public boolean isColliding(int x, int y) {
        return !isOutOfBounds(x, y) && collisionGrid[y][x] == 1;
    }

    public int getWidth() {
        return template.getWidth();
    }

    public int getHeight() {
        return template.getHeight();
    }

    public Map<Integer, Area> getPlayerStartingAreas() {
        return playerStartingAreas;
    }

    public Map<Integer, Area> getPlayerSpawnAreas() {
        return playerSpawnAreas;
    }

    public Map<Position, Type> getStaticObjectSpawns() {
        return staticObjectSpawns;
    }

    public Instance createInstance(int maxPlayers) {
        Instance instance = new Instance(instanceCount, this, maxPlayers);
        addInstance(instance);
        return instance;
    }

    private boolean addInstance(Instance instance) {
        if (instancesById.put(instance.getId(), instance) == null) {
            instanceCount++;
            return true;
        }
        return false;
    }

    public void destroyInstance(Instance instance) {
        removeInstance(instance);
    }

    private boolean removeInstance(Instance instance) {
        if (instancesById.remove(instance.getId()) != null) {
            instanceCount--;
            return true;
        }
        return false;
    }

    public Map<Integer, Instance> getInstances() {
        return instancesById;
    }
}
