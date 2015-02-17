package com.acme.server.component;

import com.acme.engine.timer.Timer;
import com.acme.server.util.Rnd;
import com.acme.server.world.Area;
import com.acme.server.world.Position;

public class Spawn extends Timer {

    private Position spawnPosition;
    private Area area;

    public Position getSpawnPosition() {
        return spawnPosition;
    }

    public void setSpawnPosition(Position spawnPosition) {
        this.spawnPosition = spawnPosition;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public void refreshTimer() {
        setTime(Rnd.between(10000, 40000));
    }
}