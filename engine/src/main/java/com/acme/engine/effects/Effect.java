package com.acme.engine.effects;

import com.badlogic.ashley.core.Component;

public class Effect extends Component {

    public int remainingTicks;
    public float tickInterval;
    public float timeToNextTick;

    public boolean isReady() {
        return remainingTicks == 0 && timeToNextTick <= 0;
    }
}
