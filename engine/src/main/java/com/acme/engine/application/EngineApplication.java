package com.acme.engine.application;

public class EngineApplication extends ApplicationAdapter {

    private volatile Context context;

    public final void start() {
        ContextBuilder builder = new ContextBuilder(this);
        builder.setApplicationName("Engine Application Thread");
        builder.setUpdateInterval(1000 / 60);
        context = builder.build();
        context.waitForStart(0);
    }

    public final void stop() {
        context.dispose();
        context.waitForDispose(0);
    }
}
