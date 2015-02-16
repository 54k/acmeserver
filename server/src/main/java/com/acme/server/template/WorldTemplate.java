package com.acme.server.template;

import com.acme.server.entities.Type;

import java.util.List;
import java.util.Map;

public class WorldTemplate {

    private int width;
    private int height;

    private List<Integer> collisions;
    private List<DoorTemplate> doors;
    private List<CheckpointTemplate> checkpoints;
    private List<ChestAreaTemplate> chestAreas;
    private List<RoamingAreaTemplate> roamingAreas;
    private List<StaticChestTemplate> staticChests;

    private Map<Integer, Type> staticEntities;
    private int tileSize;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<Integer> getCollisions() {
        return collisions;
    }

    public List<DoorTemplate> getDoors() {
        return doors;
    }

    public List<CheckpointTemplate> getCheckpoints() {
        return checkpoints;
    }

    public List<ChestAreaTemplate> getChestAreas() {
        return chestAreas;
    }

    public List<RoamingAreaTemplate> getRoamingAreas() {
        return roamingAreas;
    }

    public List<StaticChestTemplate> getStaticChests() {
        return staticChests;
    }

    public Map<Integer, Type> getStaticEntities() {
        return staticEntities;
    }

    public int getTileSize() {
        return tileSize;
    }
}
