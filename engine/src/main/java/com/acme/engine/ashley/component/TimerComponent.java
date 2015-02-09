package com.acme.engine.ashley.component;

import com.badlogic.ashley.core.Component;

public class TimerComponent extends Component {

    private float time;
    private float initialTime;

    public TimerComponent() {
    }

    public TimerComponent(float time, float initialTime) {
        this.time = time;
        this.initialTime = initialTime;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public float getInitialTime() {
        return initialTime;
    }

    public void setInitialTime(float initialTime) {
        this.initialTime = initialTime;
    }

    public void decreaseTime(float deltaTime) {
        setTime(Math.max(0, time - deltaTime));
    }

    public void refreshTimer() {
        setTime(initialTime);
    }

    public boolean isReady() {
        return time <= 0;
    }
}
