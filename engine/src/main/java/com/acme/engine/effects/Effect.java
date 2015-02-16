package com.acme.engine.effects;

import com.badlogic.ashley.core.Component;

import java.util.concurrent.atomic.AtomicLong;

public final class Effect extends Component {

    private static final AtomicLong identityCounter = new AtomicLong();

    final String identity;
    int remainingTicks;
    float tickInterval;
    float timeToNextTick;

    /**
     * Immediate effect ctor
     */
    public Effect() {
        this(0, 0);
    }

    public Effect(int remainingTicks, float tickInterval) {
        this(generateIdentity(), remainingTicks, tickInterval);
    }

    public Effect(String identity, int remainingTicks, float tickInterval) {
        this.identity = identity;
        this.remainingTicks = remainingTicks;
        this.tickInterval = tickInterval;
        this.timeToNextTick = tickInterval;
    }

    private static String generateIdentity() {
        return "effect@" + identityCounter.getAndIncrement();
    }

    public String getIdentity() {
        return identity;
    }

    public int getRemainingTicks() {
        return remainingTicks;
    }

    public void setRemainingTicks(int remainingTicks) {
        this.remainingTicks = remainingTicks;
    }

    public float getTickInterval() {
        return tickInterval;
    }

    public void setTickInterval(float tickInterval) {
        this.tickInterval = tickInterval;
    }

    public float getTimeToNextTick() {
        return timeToNextTick;
    }

    public void setTimeToNextTick(float timeToNextTick) {
        this.timeToNextTick = timeToNextTick;
    }

    public boolean isReady() {
        return remainingTicks == 0 && timeToNextTick <= 0;
    }
}
