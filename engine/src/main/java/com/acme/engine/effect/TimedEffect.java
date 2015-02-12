package com.acme.engine.effect;

public class TimedEffect<E> extends BaseEffect<E> {

    private float stepTime;
    private int ticks;

    private float currentTime;

    public TimedEffect(float stepTime) {
        this(stepTime, -1);
    }

    public TimedEffect(float stepTime, int ticks) {
        this.stepTime = stepTime;
        this.ticks = ticks;
    }

    public void resetCurrentTime() {
        currentTime = stepTime;
    }

    public float getStepTime() {
        return stepTime;
    }

    public void setStepTime(float stepTime) {
        this.stepTime = stepTime;
    }

    public int getTicks() {
        return ticks;
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
    }

    public float getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(float currentTime) {
        this.currentTime = currentTime;
    }

    public float getDuration() {
        return ticks * currentTime;
    }

    @Override
    public void apply(E entity) {
    }

    @Override
    public final void update(E entity, float deltaTime) {
        tick(entity, deltaTime);
        signalIfReady(entity);
    }

    private void signalIfReady(E entity) {
        if (isReady()) {
            ready(entity);
        }
    }

    public boolean isReady() {
        return ticks == 0 && currentTime == 0;
    }

    private void tick(E entity, float deltaTime) {
        currentTime = Math.max(0, currentTime - deltaTime);
        if (currentTime == 0 && ticks != 0) {
            if (ticks > 0) {
                ticks--;
            }
            resetCurrentTime();
            tick(entity);
        }
    }

    public void tick(E entity) {
    }

    public void ready(E entity) {
    }

    @Override
    public void remove(E entity) {
    }
}
