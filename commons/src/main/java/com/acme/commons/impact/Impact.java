package com.acme.commons.impact;

import com.acme.ecs.core.Component;

public abstract class Impact extends Component {

    int ticks;
    float timer;
    float interval;

    public Impact() {
        this(0, 0);
    }

    public Impact(int ticks, float interval) {
        this.ticks = ticks;
        this.timer = interval;
        this.interval = interval;
    }

    boolean isReady() {
        return ticks == 0 && timer <= 0;
    }
}
