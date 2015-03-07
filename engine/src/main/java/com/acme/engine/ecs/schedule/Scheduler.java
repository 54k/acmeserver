package com.acme.engine.ecs.schedule;

import java.util.LinkedList;
import java.util.Queue;

public final class Scheduler {

    private final Queue<Runnable> scheduledTasks = new LinkedList<>();
    private final Queue<Runnable> tasksToRun = new LinkedList<>();

    public void update(float deltaTime) {
        if (scheduledTasks.isEmpty()) {
            return;
        }

        tasksToRun.addAll(scheduledTasks);
        scheduledTasks.clear();
        while (!tasksToRun.isEmpty()) {
            tasksToRun.poll().run();
        }
    }

}
