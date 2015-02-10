package com.acme.server.component;

import com.acme.server.world.Area;
import com.badlogic.ashley.core.Component;

public class PlayerComponent extends Component {

    private String name;
    private State state = State.CONNECTED;
    private Area spawnArea;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Area getSpawnArea() {
        return spawnArea;
    }

    public void setSpawnArea(Area spawnArea) {
        this.spawnArea = spawnArea;
    }

    public static enum State {
        CONNECTED,
        PLAYING
    }
}
