package com.acme.server.position;

import com.acme.commons.timer.Timer;
import com.acme.server.utils.Rnd;
import com.acme.server.world.Area;
import com.acme.server.world.Position;

public class SpawnPoint extends Timer {

    public Position lastSpawnPosition;
    public Area spawnArea;

    public SpawnPoint() {
    }

    public SpawnPoint(Area spawnArea) {
        this.spawnArea = spawnArea;
    }

    public Position getLastSpawnPosition() {
        return lastSpawnPosition;
    }

    public void setLastSpawnPosition(Position lastSpawnPosition) {
        this.lastSpawnPosition = lastSpawnPosition;
    }

    public Area getSpawnArea() {
        return spawnArea;
    }

    public void setSpawnArea(Area spawnArea) {
        this.spawnArea = spawnArea;
    }

    public void refreshTimer() {
        setTime(Rnd.between(10000, 40000));
    }
}
