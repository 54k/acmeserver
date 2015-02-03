package com.acme.gameserver.component;

import com.badlogic.ashley.core.Component;

public class PlayerComponent extends Component {

    private String name;
    private State state = State.CONNECTED;

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

    public static enum State {
        CONNECTED,
        PLAYING
    }
}
