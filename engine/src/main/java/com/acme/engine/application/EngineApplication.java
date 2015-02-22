package com.acme.engine.application;

public class EngineApplication extends ApplicationAdapter {

    private volatile Context context;

    public final void start() {
        context = new UpdateLoop(this, 60);
        context.waitForStart(0);
    }

    public final void stop() {
        context.dispose();
        context.waitForDispose(0);
    }
}
