package com.acme.engine.mechanics.timer;

public class SchedulerTask extends Scheduler implements Scheduler.Task {

    @Override
    public void run(float deltaTime) {
        update(deltaTime);
    }
}
